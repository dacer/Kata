package im.dacer.kata.ui.main.inbox

import android.view.View
import im.dacer.kata.core.model.History
import im.dacer.kata.data.model.NewsItem

interface NewsMvp {
    fun showData(newsItems: List<NewsItem>)
    fun showLoading(show: Boolean)
}