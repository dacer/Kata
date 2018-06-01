package im.dacer.kata.ui.main.inbox

import im.dacer.kata.data.model.NewsItem

interface NewsMvp {
    fun showData(newsItems: List<NewsItem>)
    fun showLoading(show: Boolean)
}