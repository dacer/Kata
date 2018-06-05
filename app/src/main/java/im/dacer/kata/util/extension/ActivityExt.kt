package im.dacer.kata.util.extension

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Toast
import im.dacer.kata.util.LogUtils

/**
 * Created by Dacer on 04/02/2018.
 */
fun Activity.toast(string: String?) {
    if (string == null) return
    runOnUiThread { Toast.makeText(this, string, Toast.LENGTH_LONG).show() }
}
fun Activity.startActivity(clazz: Class<out Activity>) {
    startActivity(Intent(this, clazz))
}
fun AppCompatActivity.setMyActionBar(toolbar: View) {
    setSupportActionBar(toolbar as Toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

fun Service.toast(string: String?, length: Int = Toast.LENGTH_LONG) {
    if (string == null) return
    Toast.makeText(this, string, length).show()
}

fun Service.toast(resId: Int, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, getString(resId), length).show()
}

fun Activity.timberAndToast(throwable: Throwable) {
    LogUtils.log(throwable, this)
}

fun Service.timberAndToast(throwable: Throwable) {
    LogUtils.log(throwable, this)
}

fun View.applyHeight(height: Int) {
    this.layoutParams.height = height
    this.layoutParams = this.layoutParams
}