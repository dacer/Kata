package im.dacer.kata.ui.main.news

import android.app.Activity
import im.dacer.kata.data.model.news.NewsItem
import im.dacer.kata.ui.base.MvpView

interface NewsMvp : MvpView {
    fun showData(newsItems: List<NewsItem>)
    fun showLoading(show: Boolean)
    fun showRefreshing(show: Boolean)
    fun getMyActivity(): Activity?
    fun showLoadingText(msg: String?)
    fun updateItem(index: Int, item: NewsItem)

    fun loadMoreComplete()
    fun loadMoreEnd()
    fun loadMoreFail()
}