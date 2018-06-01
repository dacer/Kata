package im.dacer.kata.ui.main

import android.content.Context
import com.baoyz.treasure.Treasure
import im.dacer.kata.Config
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
    private val treasure by lazy { Treasure.get(context, Config::class.java) }

    fun onResume() {
        restartListenService()
    }

    fun restartListenService() {
        if (treasure.isListenClipboard) {
            ListenClipboardService.restart(context)
        }
    }
}