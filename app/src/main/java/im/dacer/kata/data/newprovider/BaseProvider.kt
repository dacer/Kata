package im.dacer.kata.data.newprovider

import im.dacer.kata.data.model.news.EasyNews
import im.dacer.kata.data.model.news.NewsItem
import io.reactivex.Observable

abstract class BaseProvider {

    abstract fun loadLocalData(): Observable<List<NewsItem>>

    abstract fun saveOnlineListAndReturnLocal(): Observable<List<NewsItem>>

    abstract fun cacheAllNoContentArticles(): Observable<EasyNews>

    abstract fun markRead(id: String): Observable<EasyNews>
}