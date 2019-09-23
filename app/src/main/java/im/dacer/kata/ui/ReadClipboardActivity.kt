package im.dacer.kata.ui

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.kata.R
import im.dacer.kata.ui.base.BaseActivity
import im.dacer.kata.util.extension.toast
import im.dacer.kata.util.helper.SchemeHelper
import im.dacer.kata.util.helper.getLastString
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


// Since we cannot access clipboard data on Android 10 or higher without focus
// https://developer.android.com/about/versions/10/privacy/changes#clipboard-data
class ReadClipboardActivity : BaseActivity() {
    private var clipboardDisposable: Disposable? = null
    private var materialDialog: MaterialDialog? = null
    private var retryCount = 0
    private val mClipboardManager: ClipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    override fun layoutId() = R.layout.empty

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        materialDialog = MaterialDialog.Builder(this)
                .content(R.string.loading_clipboard_content)
                .progress(true, 0)
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .build()
        if (isFinishing) materialDialog?.show()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            checkClipboard(false)
        } else {
            retryCount = 0
            clipboardDisposable = Observable.interval(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { checkClipboard() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        materialDialog?.hide()
        clipboardDisposable?.dispose()
    }

    private fun checkClipboard(retry: Boolean = true) {
        val text = mClipboardManager.primaryClip?.getLastString(this)
        if (text?.isNotEmpty() == true) {
            SchemeHelper.startKata(this, text)
            finish()
        } else if (retry && retryCount >= MAX_RETRY_TIMES){
            toast(R.string.clipboard_content_not_found)
            finish()
        }
        retryCount++
    }

    companion object {
        const val MAX_RETRY_TIMES = 6
    }
}