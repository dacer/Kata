package im.dacer.kata.ui.main

import android.content.Context
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.base.BasePresenter
import javax.inject.Inject

/**
 * Created by Dacer on 13/02/2018.
 */

@ConfigPersistent
class MainPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<MainMvp>() {
    @Inject lateinit var settingUtility: SettingUtility

    fun onResume() {
        restartListenService()
    }

    fun restartListenService() {
        if (settingUtility.isListenClipboard) {
            ListenClipboardService.restart(context)
        }
    }
}