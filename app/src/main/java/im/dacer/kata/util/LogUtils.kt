package im.dacer.kata.util

import android.content.Context
import org.jetbrains.anko.toast
import timber.log.Timber

object LogUtils {

    fun log(throwable: Throwable, context: Context? = null) {
        Timber.e(throwable)
        try {
            context?.toast(throwable.message.toString())
        } catch (e: Exception) {}
    }
}