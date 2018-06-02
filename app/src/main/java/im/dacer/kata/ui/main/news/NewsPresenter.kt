package im.dacer.kata.ui.main.inbox

import android.annotation.SuppressLint
import android.content.Context
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.room.AppDatabase
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.LogUtils
import javax.inject.Inject

@ConfigPersistent
class NewsPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<NewsMvp>()  {
    @Inject lateinit var appDatabase: AppDatabase
    @Inject lateinit var newsDataManager: NewsDataManager

    @SuppressLint("CheckResult")
    fun initData() {
        mvpView?.showLoading(true)
        newsDataManager.getEasyNews().subscribe({
            mvpView?.showData(it)
            mvpView?.showLoading(false)
            appDatabase.newsDao().insertAll(it.toTypedArray())

        }, { LogUtils.log(it, context) })
    }


}