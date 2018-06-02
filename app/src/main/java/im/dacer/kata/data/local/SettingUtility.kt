package im.dacer.kata.data.local

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dacer on 01/01/2018.
 */
@Singleton
class SettingUtility @Inject constructor(private val mHelper: PreferencesHelper) {

    var isListenClipboard : Boolean
        get() = mHelper[PREF_LISTEN_CLIPBOARD, true]
        set(value) { mHelper[PREF_LISTEN_CLIPBOARD] = value }

    var isDatabaseImported : Boolean
        get() = mHelper[PREF_DATABASE_IMPORTED, false]
        set(value) { mHelper[PREF_DATABASE_IMPORTED] = value }

    var cacheMax : Int
        get() = mHelper[PREF_CACHE_MAX, 10]
        set(value) { mHelper[PREF_CACHE_MAX] = value }


    var hasShownGoYoutube : Boolean
        get() = mHelper[PREF_HAS_SHOWN_GO_YOUTUBE, false]
        set(value) { mHelper[PREF_HAS_SHOWN_GO_YOUTUBE] = value }



    companion object {
        val PREF_LISTEN_CLIPBOARD = "listenclipboard"
        val PREF_DATABASE_IMPORTED = "databaseimported"
        val PREF_CACHE_MAX = "cachemax"
        val PREF_HAS_SHOWN_GO_YOUTUBE = "hasshowngoyoutube"
    }
}