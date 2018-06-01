package im.dacer.kata.ui.main

import android.content.Context
import com.baoyz.treasure.Treasure
import im.dacer.kata.Config
import im.dacer.kata.service.ListenClipboardService

/**
 * Created by Dacer on 13/02/2018.
 */
class MainPresenter(val context: Context, private val mainMvp: MainMvp) {
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