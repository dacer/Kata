package im.dacer.kata.ui.base

import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.util.LongSparseArray
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import im.dacer.kata.App
import im.dacer.kata.injection.component.ActivityComponent
import im.dacer.kata.injection.component.ConfigPersistentComponent
import im.dacer.kata.injection.component.DaggerConfigPersistentComponent
import im.dacer.kata.injection.module.ActivityModule
import im.dacer.kata.util.LogUtils
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong






/**
 * Abstract activity that every other Activity in this application must implement. It provides the
 * following functionality:
 * - Handles creation of Dagger components and makes sure that instances of
 * ConfigPersistentComponent are kept across configuration changes.
 * - Set up and handles a GoogleApiClient instance that can be used to access the Google sign in
 * api.
 * - Handles signing out when an authentication error event is received.
 */
abstract class BaseActivity : AppCompatActivity(), MvpView {

    private var activityComponent: ActivityComponent? = null
    private var activityId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        // Create the ActivityComponent and reuses cached ConfigPersistentComponent if this is
        // being called after a configuration change.
        activityId = savedInstanceState?.getLong(KEY_ACTIVITY_ID) ?: NEXT_ID.getAndIncrement()
        val configPersistentComponent: ConfigPersistentComponent
        if (componentsArray.get(activityId) == null) {
            Timber.i("Creating new ConfigPersistentComponent id=%d", activityId)
            configPersistentComponent = DaggerConfigPersistentComponent.builder()
                    .appComponent(App[this].component)
                    .build()
            componentsArray.put(activityId, configPersistentComponent)
        } else {
            Timber.i("Reusing ConfigPersistentComponent id=%d", activityId)
            configPersistentComponent = componentsArray.get(activityId)
        }
        activityComponent = configPersistentComponent.activityComponent(ActivityModule(this))
        activityComponent?.inject(this)

    }

    @LayoutRes abstract fun layoutId(): Int

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_ACTIVITY_ID, activityId)
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            Timber.i("Clearing ConfigPersistentComponent id=%d", activityId)
            componentsArray.remove(activityId)
        }
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun toastError(t: Throwable) {
        LogUtils.log(t, this)
    }

    fun activityComponent() = activityComponent as ActivityComponent


    protected open fun hideSystemUI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    protected open fun showSystemUI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
    companion object {
        private val KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID"
        private val NEXT_ID = AtomicLong(0)
        private val componentsArray = LongSparseArray<ConfigPersistentComponent>()
    }
}