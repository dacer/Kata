package im.dacer.kata.ui.main.inbox

import android.app.Activity
import android.view.View
import im.dacer.kata.data.model.bigbang.History
import im.dacer.kata.ui.base.MvpView

interface InboxMvp : MvpView {
    fun setBigbangTipTv(strId: Int)
    fun catchError(throwable: Throwable)
    fun showNothingHappenedView()
    fun showGoYoutubeView()
    fun getClipTvText(): String
    fun showHistory(historyList: List<History>?)
    fun getDecorView(): View
    fun updateHistory(index: Int, history: History)
    val activity: Activity?
}