package im.dacer.kata.ui.main.inbox

import android.view.View
import im.dacer.kata.core.model.History

interface InboxMvp {
    fun setBigbangTipTv(strId: Int)
    fun catchError(throwable: Throwable)
    fun showNothingHappenedView()
    fun showGoYoutubeView()
    fun getClipTvText(): String
    fun showHistory(historyList: List<im.dacer.kata.core.model.History>?)
    fun getDecorView(): View
    fun updateHistory(index: Int, history: im.dacer.kata.core.model.History)
}