package im.dacer.kata.service

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.util.NotificationUtil
import im.dacer.kata.util.engine.SegmentEngine
import im.dacer.kata.util.extension.findUrl
import im.dacer.kata.util.helper.SchemeHelper
import im.dacer.kata.util.helper.getLastString
import im.dacer.kata.util.helper.hasKanjiOrKana
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class ListenClipboardService : Service() {

    private val mClipboardManager: ClipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    private val mOnPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener { showAction() }
    private val appPref: MultiprocessPref by lazy { MultiprocessPref(this) }
    private var disableDis: Disposable? = null

    private var lastShowActionTime = 0L
    private var lastShowActionText = ""
    private fun showAction() {
        val primaryClip = mClipboardManager.primaryClip
        val text = primaryClip?.getLastString(this)
        if (text?.isNotEmpty() == true) {
            if (System.currentTimeMillis() - lastShowActionTime < TimeUnit.SECONDS.toMillis(2) &&
                    lastShowActionText == text.toString()) {
                return
            }
            lastShowActionText = text.toString()
            lastShowActionTime = System.currentTimeMillis()

            if (appPref.useWebParser &&
                    text.toString().findUrl() != null &&
                    appPref.analyzeUrlInClipboard) {
                SchemeHelper.startKataFloatDialog(this, text.toString())
                return
            }

            //Only data from clipboard need check whether kanji inside
            if (!text.toString().hasKanjiOrKana()) { return }

            SchemeHelper.startKataFloatDialog(this, text.toString())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val foreground = intent?.getBooleanExtra(EXTRA_FOREGROUND, false)
        if (foreground == true) {
            startForeground(NotificationUtil.NOTIFICATION_ID, NotificationUtil.getNotification(this))
        }
        disableDis?.dispose()
        val disableTimeInSec = intent?.getLongExtra(EXTRA_TEMP_DISABLE_SECOND, 0L)
        if (disableTimeInSec != null && disableTimeInSec != 0L) {
            mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener)
            disableDis = Observable.timer(disableTimeInSec, TimeUnit.SECONDS)
                    .subscribe{ mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener) }
        }
        return START_REDELIVER_INTENT
    }

    override fun onCreate() {
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener)
        Observable.fromCallable { SegmentEngine.setup() }.subscribeOn(Schedulers.io()).subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        disableDis?.dispose()
        mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener)
    }

    override fun onBind(intent: Intent): IBinder? = null


    companion object {
        const val EXTRA_TEMP_DISABLE_SECOND = "extra_temp_disable_second"
        const val EXTRA_FOREGROUND = "extra_foreground"

        fun start(context: Context) {
            val pref = MultiprocessPref(context)
            val foreground = pref.enhancedMode
            val serviceIntent = Intent(context, ListenClipboardService::class.java)
            serviceIntent.putExtra(EXTRA_FOREGROUND, foreground)
            if (foreground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }

        fun disable(context: Context, disableTimeInSecond: Long) {
            val serviceIntent = Intent(context, ListenClipboardService::class.java)
                    .putExtra(EXTRA_TEMP_DISABLE_SECOND, disableTimeInSecond)
            context.startService(serviceIntent)
        }

        fun stop(context: Context) {
            val serviceIntent = Intent(context, ListenClipboardService::class.java)
            context.stopService(serviceIntent)
        }

        fun restart(context: Context) {
            stop(context)
            start(context)
        }


    }

}