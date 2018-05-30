package im.dacer.kata.core.extension

import android.app.Activity
import android.app.Fragment
import android.app.Service
import android.widget.Toast
import org.jetbrains.anko.toast
import timber.log.Timber

/**
 * Created by Dacer on 04/02/2018.
 */
fun Activity.toast(string: String?) {
    if (string == null) return
    runOnUiThread { Toast.makeText(this, string, Toast.LENGTH_LONG).show() }
}
fun Service.toast(string: String?, length: Int = Toast.LENGTH_LONG) {
    if (string == null) return
    Toast.makeText(this, string, length).show()
}

fun Service.toast(resId: Int, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, getString(resId), length).show()
}

fun Activity.timberAndToast(throwable: Throwable) {
    Timber.e(throwable)
    toast(throwable.message)
}

fun Service.timberAndToast(throwable: Throwable) {
    Timber.e(throwable)
    toast(throwable.message)
}