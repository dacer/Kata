package im.dacer.kata.util.helper

import im.dacer.kata.BuildConfig

object ConfigHelper {
    fun isCoolApk(): Boolean {
        return BuildConfig.FLAVOR == "coolapk"
    }
}