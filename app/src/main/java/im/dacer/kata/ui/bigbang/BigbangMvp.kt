package im.dacer.kata.ui.bigbang

import android.app.Activity
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.ui.base.MvpView

/**
 * Created by Dacer on 13/02/2018.
 */
interface BigbangMvp : MvpView {
    fun onDataInitFinished(list: List<KanjiResult>, preselectedIndex: Int?)
    fun showSystemUI()
    fun resetMeaningViewPos()
    fun resetBigBangScrollViewPos()
    fun finish()
    fun showVoiceBtn(url: String)

    var descText: String?
    var meaningText: String
    var pronunciationText: String?
    val activity: Activity
}