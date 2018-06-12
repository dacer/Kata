package im.dacer.kata.ui.main.news

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.dinuscxj.refresh.RecyclerRefreshLayout
import im.dacer.kata.R
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.news.EasyNews
import im.dacer.kata.data.model.news.NewsItem
import im.dacer.kata.data.room.AppDatabase
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.service.UrlAnalysisService
import im.dacer.kata.ui.VideoPlayerActivity
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.LogUtils
import im.dacer.kata.util.helper.SchemeHelper
import im.dacer.kata.util.webparse.WebParser
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import timber.log.Timber
import javax.inject.Inject

@ConfigPersistent
class NewsPresenter @Inject constructor(@ApplicationContext val context: Context) :
        BasePresenter<NewsMvp>(), RecyclerRefreshLayout.OnRefreshListener {

    @Inject lateinit var appDatabase: AppDatabase
    @Inject lateinit var newsDataManager: NewsDataManager
    @Inject lateinit var settingUtility: SettingUtility

    private val pref by lazy { MultiprocessPref(context) }
    private var initDataDisposable: Disposable? = null
    private var fetchDataDisposable: Disposable? = null
    private var cacheDisposable: Disposable? = null

    fun initData() {
        mvpView?.showLoading(true)
        initDataDisposable?.dispose()
        initDataDisposable = appDatabase.newsDao().loadAll()
                .take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    mvpView?.showData(it)
                    if (!it.isEmpty()) mvpView?.showLoading(false)
                    fetchData(it.isNotEmpty())
                }, { log(it) }, {}, { it.request(1) })
    }

    private fun fetchData(hasData: Boolean = true) {
        if (!networkConnected()) {
            onFetchFinished(null)
            if (!hasData) context.toast(R.string.no_internet)
            return
        }
        mvpView?.showLoadingText(context.getString(R.string.syncing))
        initDataDisposable?.dispose()
        fetchDataDisposable?.dispose()
        cacheDisposable?.dispose()
        fetchDataDisposable = newsDataManager.getEasyNews()
                .subscribe({
                    onFetchFinished(it)
                    mvpView?.showLoadingText(null)
                    if (!(!isWifi() && settingUtility.newsCachingWifiOnly)) {
                        cacheAllData()
                    }
                }, { log(it) })
    }

    private var nowSyncingSize = 0
    private fun cacheAllData() {
        cacheDisposable?.dispose()
        nowSyncingSize = 0
        mvpView?.showLoadingText(context.getString(R.string.caching_articles))
        cacheDisposable = appDatabase.newsDao().loadAllNoContent()
                .take(1)
                .concatMap { Flowable.fromIterable(it).onBackpressureBuffer() }
                .filter { it.content.isNullOrEmpty() }
                .concatMap { WebParser.fetchNewsContent(it, pref).toFlowable(BackpressureStrategy.BUFFER).onExceptionResumeNext(Flowable.empty()) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.e("${it.id()} : ${it.title}")
                    appDatabase.newsDao().updateNews(it)
                    nowSyncingSize++
                    mvpView?.showLoadingText("${context.getString(R.string.caching_articles)} $nowSyncingSize")

                }, { log(it) }, { mvpView?.showLoadingText(null) })
    }


    fun onNewsItemClicked(item: NewsItem?) {
        val id = item?.id() ?: return

        appDatabase.newsDao().get(id).take(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.content.isNullOrEmpty()) {
                        mvpView?.getMyActivity()?.run {
                            startService(UrlAnalysisService.getIntent(this, it.link()!!, false))
                        }
                    } else {
                        SchemeHelper.startKata(context, it.content!!)
                    }
                }
    }



    fun onPlayVideoClicked(item: NewsItem?) {
        if (item?.videoUrl().isNullOrEmpty()) {

        } else {
            mvpView?.getMyActivity()?.run { startActivity(VideoPlayerActivity.getIntent(this, item!!.videoUrl()!!)) }
        }
    }

    private fun onFetchFinished(newsList: ArrayList<EasyNews>?) {
        mvpView?.showLoading(false)
        mvpView?.showRefreshing(false)
        newsList?.run {
            mvpView?.showData(this)
            appDatabase.newsDao().insertAll(this.toTypedArray())
        }
    }

    override fun onRefresh() {
        fetchData()
    }

    override fun detachView() {
        super.detachView()
        initDataDisposable?.dispose()
        fetchDataDisposable?.dispose()
        cacheDisposable?.dispose()
    }

    private fun log(exception: Throwable) {
        LogUtils.log(exception, context)
        mvpView?.showLoadingText(null)
        mvpView?.showLoading(false)
    }

    private fun networkConnected() : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun isWifi(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.type == ConnectivityManager.TYPE_WIFI
    }
}