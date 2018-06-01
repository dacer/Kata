package im.dacer.kata.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.service.ListenClipboardService

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pref = MultiprocessPref(context)
        // Based on https://developer.android.com/about/versions/oreo/background#services
        // This broadcast cannot start a background service
        // So, if enhancedMode is off, we will not try to start clipboardService on Android O
        if (!pref.enhancedMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) return

        ListenClipboardService.start(context)
    }
}
