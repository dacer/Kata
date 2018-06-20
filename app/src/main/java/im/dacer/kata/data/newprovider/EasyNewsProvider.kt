package im.dacer.kata.data.newprovider

import android.content.Context
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.news.EasyNews
import im.dacer.kata.data.model.news.NewsItem
import im.dacer.kata.data.room.dao.EasyNewsDao
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.util.webparse.WebParser
import io.reactivex.Observable
import javax.inject.Inject

class EasyNewsProvider @Inject constructor(@ApplicationContext val context: Context): BaseProvider() {

    @Inject lateinit var easyNewsDao: EasyNewsDao
    @Inject lateinit var newsDataManager: NewsDataManager
    @Inject lateinit var settingUtility: SettingUtility
    private val pref by lazy { MultiprocessPref(context) }


    override fun loadLocalData(): Observable<List<NewsItem>> {
        return easyNewsDao.loadAll()
                .take(1)
                .map { it.map { it as NewsItem } }
                .toObservable()
    }

    override fun saveOnlineListAndReturnLocal(): Observable<List<NewsItem>> {
        return newsDataManager.getEasyNews()
                .doOnNext{easyNewsDao.insertAll(it.toTypedArray())}
                .concatMap {  easyNewsDao.loadAll().take(1).toObservable() }
    }

    override fun cacheAllNoContentArticles(): Observable<NewsItem> {
        return easyNewsDao.loadAllNoContent()
                .take(1)
                .toObservable()
                .concatMap { Observable.fromIterable(it) }
                .filter { it.content.isNullOrEmpty() }
                .concatMap { WebParser<EasyNews>().fetchNewsContent(it, pref).onExceptionResumeNext(Observable.empty()) }
                .doOnNext { easyNewsDao.updateNews(it) }
                .map { it as NewsItem }
    }

    override fun markRead(id: String): Observable<NewsItem> {
        return easyNewsDao.get(id).take(1)
                .map {
                    it.hasRead = true
                    easyNewsDao.updateNews(it)
                    return@map it as NewsItem
                }
                .toObservable()
    }

    override fun loadMoreAndCache(): Observable<List<NewsItem>> {
        return Observable.empty()
    }


}