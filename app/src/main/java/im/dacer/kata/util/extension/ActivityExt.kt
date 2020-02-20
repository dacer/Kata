package im.dacer.kata.util.extension

import android.app.Activity
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.DisplayMetrics
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import im.dacer.kata.util.LogUtils

/**
 * Created by Dacer on 04/02/2018.
 */
fun Activity.toast(string: String?) {
    if (string == null) return
    runOnUiThread { Toast.makeText(this, string, Toast.LENGTH_LONG).show() }
}
fun Activity.toast(resId: Int) {
    toast(getString(resId))
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

fun View.onRendered(listener: (Unit) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            listener(Unit)
        }
    })
}

fun View.setPaddingBottom(newPadding: Int) {
    setPadding(paddingLeft, paddingTop, paddingRight, newPadding)
}

// Make sure the view has been rendered before call this method
fun Activity.getNavBarHeight(): Int {
    val result = 0
    val hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
    val hasNavBar = hasNavBar(window.decorView)
    if (!hasMenuKey && !hasBackKey && hasNavBar) {
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

// Make sure the view has been rendered before call this method
fun Fragment.getNavBarHeight(): Int {
    return activity?.getNavBarHeight() ?: 0
}

fun Activity.hasNavBar(rootView: View?): Boolean {
    if (rootView == null)
        return true

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
        return true

    val d = windowManager.defaultDisplay
    val realDisplayMetrics = DisplayMetrics()
    d.getRealMetrics(realDisplayMetrics)

    val viewHeight = rootView.height
    if (viewHeight == 0)
        return true

    val realHeight = realDisplayMetrics.heightPixels
    return realHeight != viewHeight
}

fun Activity.isTablet(): Boolean {
    return resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}

fun Activity.openGooglePlay(packageName: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")))
    } catch (e: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
    }
}

fun Context.isWifi(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    return activeNetwork?.type == ConnectivityManager.TYPE_WIFI
}

fun Context.isInstalled(packageName: String): Boolean {
    return try {
        packageManager.getApplicationInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}