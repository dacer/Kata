package im.dacer.kata.ui.main.news

import im.dacer.kata.data.model.NewsItem
import im.dacer.kata.ui.base.MvpView

interface NewsMvp : MvpView {
    fun showData(newsItems: List<NewsItem>)
    fun showLoading(show: Boolean)
}