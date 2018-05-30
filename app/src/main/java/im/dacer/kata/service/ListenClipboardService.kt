package im.dacer.kata.service

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import im.dacer.kata.SegmentEngine
import im.dacer.kata.core.data.MultiprocessPref
import im.dacer.kata.core.extension.findUrl
import im.dacer.kata.core.util.NotificationUtil
import im.dacer.kata.core.util.SchemeHelper
import im.dacer.kata.segment.util.hasKanjiOrKana
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class ListenClipboardService : Service() {

    private val mClipboardManager: ClipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    private val mOnPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener { showAction() }
    private val appPref: MultiprocessPref by lazy { MultiprocessPref(this) }
    private var disableDis: Disposable? = null

    private fun showAction() {
        val primaryClip = mClipboardManager.primaryClip
        if (primaryClip != null && primaryClip.itemCount > 0 && "BigBang" != primaryClip.description.label) {
            val text = primaryClip.getItemAt(0).coerceToText(this)
            if (text.isEmpty()) { return }

            if (appPref.useWebParser && text.toString().findUrl() != null) {
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
        Observable.fromCallable { SegmentEngine.setup(this) }.subscribeOn(Schedulers.io()).subscribe()
    }



    override fun onDestroy() {
        super.onDestroy()
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