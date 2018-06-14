package im.dacer.kata.util.extension

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.ViewConfiguration
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

fun Activity.getNavBarHeight(): Int {
    val result = 0
    val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)

    if (!hasMenuKey && !hasBackKey) {
        //The device has a navigation bar
        val resources = resources

        val orientation = resources.configuration.orientation
        val resourceId: Int
        resourceId = if (isTablet()) {
            resources.getIdentifier(if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_height_landscape", "dimen", "android")
        } else {
            resources.getIdentifier(if (orientation == Configuration.ORIENTATION_PORTRAIT) "navigation_bar_height" else "navigation_bar_width", "dimen", "android")
        }

        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId)
        }
    }
    return result
}


fun Activity.isTablet(): Boolean {
    return resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}


fun Context.isWifi(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.type == ConnectivityManager.TYPE_WIFI
}