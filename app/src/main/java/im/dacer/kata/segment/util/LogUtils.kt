package im.dacer.kata.segment.util

import android.content.Context
import com.crashlytics.android.Crashlytics
import org.jetbrains.anko.toast
import timber.log.Timber

object LogUtils {

    fun log(throwable: Throwable, context: Context? = null) {
        Timber.e(throwable)
        Crashlytics.logException(throwable)
        try {
            context?.toast(throwable.message.toString())
        } catch (e: Exception) {}
    }
}