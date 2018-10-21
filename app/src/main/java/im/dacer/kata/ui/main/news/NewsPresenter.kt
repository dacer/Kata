package im.dacer.kata.ui.main.news

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.dinuscxj.refresh.RecyclerRefreshLayout
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.news.NewsItem
import im.dacer.kata.data.newprovider.BaseProvider
import im.dacer.kata.data.newprovider.EasyNewsProvider
import im.dacer.kata.data.newprovider.NhkNewsProvider
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.service.UrlAnalysisService
import im.dacer.kata.ui.VideoPlayerActivity
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.LogUtils
import im.dacer.kata.util.extension.isWifi
import im.dacer.kata.util.helper.SchemeHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import javax.inject.Inject

@ConfigPersistent
class NewsPresenter @Inject constructor(@ApplicationContext val context: Context) :
        BasePresenter<NewsMvp>(), RecyclerRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @Inject lateinit var easyNewsProvider: EasyNewsProvider
    @Inject lateinit var nhkNewsProvider: NhkNewsProvider
    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var multiprocessPref: MultiprocessPref

    var newsType = NewsFragment.NewsType.NHK_EASY
    private var initDataDisposable: Disposable? = null
    private var fetchDataDisposable: Disposable? = null
    private var cacheDisposable: Disposable? = null
    private var loadMoreDisposable: Disposable? = null


    private fun newsProvider(): BaseProvider {
        return when(newsType) {
            NewsFragment.NewsType.NHK_EASY -> easyNewsProvider
            NewsFragment.NewsType.NHK -> nhkNewsProvider
        }
    }

    fun initData() {
        mvpView?.showLoading(true)
        initDataDisposable?.dispose()
        initDataDisposable = newsProvider().loadLocalData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    mvpView?.showData(it)
                    if (!it.isEmpty()) mvpView?.showLoading(false)
                    fetchInitData(it.isNotEmpty())
                }, { log(it) }, {}).addToComposite()
    }


    override fun onLoadMoreRequested() {
        if (!networkConnected(showErrorToast = true)) {
            mvpView?.loadMoreFail()
            return
        }
        loadMoreDisposable?.dispose()
        loadMoreDisposable = newsProvider().loadMoreAndCache()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mvpView?.addItems(it)
                    mvpView?.loadMoreComplete()
                    cacheAllData()
                }, {
                    mvpView?.loadMoreEnd()
                    LogUtils.log(it)
                }, {}).addToComposite()
    }

    private fun fetchInitData(hasData: Boolean = true) {
        if (!networkConnected()) {
            onFetchFinished()
            if (!hasData) context.toast(R.string.no_internet)
            return
        }
        mvpView?.showLoadingText(context.getString(R.string.syncing))
        initDataDisposable?.dispose()
        fetchDataDisposable?.dispose()
        cacheDisposable?.dispose()
        fetchDataDisposable = newsProvider().saveOnlineListAndReturnLocal()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mvpView?.showData(it)
                    onFetchFinished()
                    mvpView?.showLoadingText(null)
                    cacheAllData()
                }, { log(it) }).addToComposite()
    }

    private var nowSyncingSize = 0
    private fun cacheAllData() {
        if (!context.isWifi() && multiprocessPref.newsCachingWifiOnly) return
        cacheDisposable?.dispose()
        nowSyncingSize = 0
        mvpView?.showLoadingText(context.getString(R.string.caching_articles))
        cacheDisposable = newsProvider().cacheAllNoContentArticles()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    nowSyncingSize++
                    mvpView?.showLoadingText("${context.getString(R.string.caching_articles)} $nowSyncingSize")

                }, { log(it) }, { mvpView?.showLoadingText(null) }).addToComposite()
    }


    fun onNewsItemClicked(index: Int, item: NewsItem?) {
        val id = item?.id() ?: return
        newsProvider().markRead(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.content().isNullOrEmpty()) {
                        mvpView?.getMyActivity()?.run {
                            startService(UrlAnalysisService.getIntent(this, it.link()!!, false, it.voiceUrl()))
                        }
                    } else {
                        SchemeHelper.startKata(context, it.content()!!, saveInHistory = false, voiceUrl = it.voiceUrl())
                    }
                    mvpView?.updateItem(index, it)
                }
    }



    fun onPlayVideoClicked(item: NewsItem?) {
        if (item?.videoUrl().isNullOrEmpty()) {

        } else {
            mvpView?.getMyActivity()?.run { startActivity(VideoPlayerActivity.getIntent(this, item!!.videoUrl()!!)) }
        }
    }

    private fun onFetchFinished() {
        mvpView?.showLoading(false)
        mvpView?.showRefreshing(false)
    }

    override fun onRefresh() {
        fetchInitData()
    }

    override fun detachView() {
        super.detachView()
    }

    private fun log(exception: Throwable) {
        LogUtils.log(exception, context)
        mvpView?.showLoadingText(null)
        mvpView?.showLoading(false)
    }

    private fun networkConnected(showErrorToast: Boolean = false) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val result = activeNetwork?.isConnectedOrConnecting == true
        if (!result && showErrorToast) {
            context.toast(R.string.no_internet)
        }
        return result
    }

}