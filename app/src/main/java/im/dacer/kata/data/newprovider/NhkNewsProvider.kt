package im.dacer.kata.data.newprovider

import android.content.Context
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.news.NewsItem
import im.dacer.kata.data.model.news.NhkNews
import im.dacer.kata.data.room.NhkNewsDao
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.util.webparse.WebParser
import io.reactivex.Observable
import javax.inject.Inject

class NhkNewsProvider @Inject constructor(@ApplicationContext val context: Context): BaseProvider() {

    @Inject lateinit var nhkNewsDao: NhkNewsDao
    @Inject lateinit var newsDataManager: NewsDataManager
    @Inject lateinit var settingUtility: SettingUtility
    private val pref by lazy { MultiprocessPref(context) }


    override fun loadLocalData(): Observable<List<NewsItem>> {
        return nhkNewsDao.loadAll()
                .take(1)
                .map { it.map { it as NewsItem } }
                .toObservable()
    }

    override fun saveOnlineListAndReturnLocal(): Observable<List<NewsItem>> {
        return newsDataManager.getNhkNews()
                .doOnNext{nhkNewsDao.insertAll(it.toTypedArray())}
                .concatMap {  nhkNewsDao.loadAll().take(1).toObservable() }
    }

    override fun cacheAllNoContentArticles(): Observable<NewsItem> {
        return nhkNewsDao.loadAllNoContent()
                .take(1)
                .toObservable()
                .concatMap { Observable.fromIterable(it) }
                .filter { it.content.isNullOrEmpty() }
                .concatMap { WebParser<NhkNews>().fetchNewsContent(it, pref).onExceptionResumeNext(Observable.empty()) }
                .doOnNext { nhkNewsDao.updateNews(it) }
                .map { it as NewsItem }
    }

    override fun markRead(id: String): Observable<NewsItem> {
        return nhkNewsDao.get(id).take(1)
                .map {
                    it.hasRead = true
                    nhkNewsDao.updateNews(it)
                    return@map it as NewsItem
                }
                .toObservable()
    }


}