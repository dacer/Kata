package im.dacer.kata.ui.main.inbox

import android.content.Context
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.room.NewsRoomStore
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.LogUtils
import javax.inject.Inject

@ConfigPersistent
class NewsPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<NewsMvp>()  {
    private val newsRoomStore by lazy { NewsRoomStore(context) }

    fun initData() {
        mvpView?.showLoading(true)
//        NewsDataManager.getEasyNews().subscribe({
//            newsMvp.showData(it)
//            newsMvp.showLoading(false)
//            newsRoomStore.db.newsDao().insertAll(it.toTypedArray())
//
//        }, { LogUtils.log(it, context) })
    }


}