package im.dacer.kata.ui.main.news

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.dinuscxj.refresh.RecyclerRefreshLayout
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.model.EasyNews
import im.dacer.kata.data.model.NewsItem
import im.dacer.kata.data.room.AppDatabase
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.service.UrlAnalysisService
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ConfigPersistent
class NewsPresenter @Inject constructor(@ApplicationContext val context: Context) :
        BasePresenter<NewsMvp>(), RecyclerRefreshLayout.OnRefreshListener {

    @Inject lateinit var appDatabase: AppDatabase
    @Inject lateinit var newsDataManager: NewsDataManager
    private var initDataDisposable: Disposable? = null
    private var fetchDataDisposable: Disposable? = null

    fun initData() {
        mvpView?.showLoading(true)
        initDataDisposable?.dispose()

        initDataDisposable = appDatabase.newsDao().loadAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    mvpView?.showData(it)
                    if (!it.isEmpty()) mvpView?.showLoading(false)
                    fetchData()
                }
    }

    fun fetchData() {
        if (!networkConnected()) {
            onFetchFinished(null)
            return
        }
        //todo add a setting for WIFI ONLY
        fetchDataDisposable?.dispose()
        fetchDataDisposable = newsDataManager.getEasyNews()
                .subscribe({
                    onFetchFinished(it)
                }, { LogUtils.log(it, context) })
    }

    fun onNewsItemClicked(item: NewsItem?) {
        val link = item?.link()
        if (link.isNullOrEmpty()) return
        mvpView?.getMyActivity()?.run {
            startService(UrlAnalysisService.getIntent(this, link!!))
        }
    }

    fun onPlayVideoClicked(item: NewsItem?) {

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