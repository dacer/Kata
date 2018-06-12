package im.dacer.kata.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.widget.Toast
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.util.extension.timberAndToast
import im.dacer.kata.util.extension.toast
import im.dacer.kata.util.helper.SchemeHelper
import im.dacer.kata.util.webparse.EasyNewsParser
import im.dacer.kata.util.webparse.WebParser
import im.dacer.kata.view.FloatingLoadingView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Dacer on 07/02/2018.
 */
class UrlAnalysisService : Service() {
    private var disposable: Disposable? = null
    private val floatingView by lazy { FloatingLoadingView(this) }
    private val pref by lazy { MultiprocessPref(this) }
    private var lastCancelTimeInMillis = 0L

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        floatingView.show()
        floatingView.setOnClickListener {
            if (System.currentTimeMillis() - lastCancelTimeInMillis <=
                    TimeUnit.SECONDS.toMillis(TEMP_DISABLE_TIME_IN_SEC)) {
                toast(R.string.disable_for_one_minute_tip)
            } else {
                toast(R.string.cancelled, Toast.LENGTH_SHORT)
            }

            disposable?.dispose()
            floatingView.dismiss()
            lastCancelTimeInMillis = System.currentTimeMillis()
//            stopSelf()
        }
        floatingView.setOnLongClickListener {
            disposable?.dispose()
            floatingView.dismiss()
            toast(R.string.disable_for_one_minute, Toast.LENGTH_SHORT)
            disableForOneMin()
        }
        if (intent?.getStringExtra(EXTRA_URL) != null) {
            fetchUrlContent(intent.getStringExtra(EXTRA_URL), intent.getBooleanExtra(SAVE_IN_HISTORY, true))
        }
        return START_NOT_STICKY
    }

    private fun disableForOneMin(): Boolean {
        ListenClipboardService.disable(this, TEMP_DISABLE_TIME_IN_SEC)
        return true
    }

    private fun fetchUrlContent(url: String, saveInHistory: Boolean) {
        toast(R.string.fetching_content_from_web_page, Toast.LENGTH_SHORT)
        disposable?.dispose()
        disposable = WebParser.fetchContent(url, pref)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    floatingView.dismiss()
                    SchemeHelper.startKata(this, it, saveInHistory = saveInHistory)
                    stopSelf()
                }, {
                    if (it is EasyNewsParser.ContentNotFound) {
                        toast(R.string.web_404_error)
                    } else {
                        timberAndToast(it)
                    }
                    floatingView.dismiss()
                })

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        floatingView.dismiss()
    }

    companion object {
        private const val EXTRA_URL = "url"
        private const val SAVE_IN_HISTORY = "save_in_history"
        private const val TEMP_DISABLE_TIME_IN_SEC = 60L

        fun getIntent(c: Context, url: String, saveInHistory: Boolean = true): Intent =
                Intent(c, UrlAnalysisService::class.java)
                        .putExtra(EXTRA_URL, url)
                        .putExtra(SAVE_IN_HISTORY,  saveInHistory)
    }
}