package im.dacer.kata.data.newprovider

import im.dacer.kata.data.model.news.NewsItem
import io.reactivex.Observable

abstract class BaseProvider {
    var page = 1

    abstract fun loadLocalData(): Observable<List<NewsItem>>

    abstract fun saveOnlineListAndReturnLocal(): Observable<List<NewsItem>>

    abstract fun cacheAllNoContentArticles(): Observable<NewsItem>

    abstract fun markRead(id: String): Observable<NewsItem>

    abstract fun loadMoreAndCache(): Observable<List<NewsItem>>
}