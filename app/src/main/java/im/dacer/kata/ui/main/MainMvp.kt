package im.dacer.kata.ui.main

import android.app.Activity
import im.dacer.kata.ui.base.MvpView

/**
 * Created by Dacer on 13/02/2018.
 */
interface MainMvp : MvpView {
    fun getActivity(): Activity
}