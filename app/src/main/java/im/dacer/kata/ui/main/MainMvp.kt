package im.dacer.kata.ui.main

import android.view.View
import im.dacer.kata.core.model.History

/**
 * Created by Dacer on 13/02/2018.
 */
interface MainMvp {
    fun setBigbangTipTv(strId: Int)
    fun catchError(throwable: Throwable)
    fun showNothingHappenedView()
    fun showGoYoutubeView()
    fun getClipTvText(): String
    fun showHistory(historyList: List<History>?)
    fun getDecorView(): View
    fun updateHistory(index: Int, history: History)
}