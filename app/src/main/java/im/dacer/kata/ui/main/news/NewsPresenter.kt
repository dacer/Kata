package im.dacer.kata.ui.main.inbox

import android.content.Context
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.room.NewsRoomStore
import im.dacer.kata.util.LogUtils

class NewsPresenter(val context: Context, private val newsMvp: NewsMvp) {
    private val newsRoomStore by lazy { NewsRoomStore(context) }

    fun initData() {
        newsMvp.showLoading(true)
        NewsDataManager.getEasyNews().subscribe({
            newsMvp.showData(it)
            newsMvp.showLoading(false)
            newsRoomStore.db.newsDao().insertAll(it.toTypedArray())

        }, { LogUtils.log(it, context) })
    }


    fun onDestroy() {
    }
}