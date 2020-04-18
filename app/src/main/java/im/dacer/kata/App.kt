package im.dacer.kata

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.androidnetworking.AndroidNetworking
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.tspoon.traceur.Traceur
import im.dacer.kata.injection.component.AppComponent
import im.dacer.kata.injection.component.DaggerAppComponent
import im.dacer.kata.injection.module.AppModule
import io.fabric.sdk.android.Fabric
import okhttp3.OkHttpClient
import timber.log.Timber

class App : MultiDexApplication() {

    private var appComponent: AppComponent? = null

    var component: AppComponent
        get() {
            if (appComponent == null) {
                appComponent = DaggerAppComponent.builder()
                        .appModule(AppModule(this))
                        .build()
            }
            return appComponent as AppComponent
        }
        set(appComponent) {
            this.appComponent = appComponent
        }

    companion object {
        operator fun get(context: Context): App {
            return context.applicationContext as App
        }
    }

    override fun onCreate() {
        super.onCreate()
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return
//        }
        Fabric.with(this, Crashlytics.Builder().core(CrashlyticsCore.Builder().build()).build())

        val okHttpClient = OkHttpClient().newBuilder()

        if (BuildConfig.DEBUG) {
//            LeakCanary.install(this)
            Timber.plant(Timber.DebugTree())
            Traceur.enableLogging()
            Stetho.initializeWithDefaults(this)
            okHttpClient.addNetworkInterceptor(StethoInterceptor())
        } else {
            Timber.plant(CrashReportingTree())
        }

        AndroidNetworking.initialize(applicationContext, okHttpClient.build())
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            Crashlytics.logException(t)
        }
    }
}
