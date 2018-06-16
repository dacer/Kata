package im.dacer.kata.data.local

import im.dacer.kata.ui.main.MainActivity
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

    var newsCachingWifiOnly : Boolean
        get() = mHelper[PREF_NEWS_CACHING_WIFI_ONLY, true]
        set(value) { mHelper[PREF_NEWS_CACHING_WIFI_ONLY] = value }

    var lastExitTab : Long
        get() = mHelper[PREF_LAST_EXIT_TAB, MainActivity.DrawerItem.INBOX.id]
        set(value) { mHelper[PREF_LAST_EXIT_TAB] = value }

    var demoAlertHasShown : Boolean
        get() = mHelper[DEMO_ALERT, false]
        set(value) { mHelper[DEMO_ALERT] = value }


    companion object {
        private const val PREF_LISTEN_CLIPBOARD = "listenclipboard"
        private const val PREF_DATABASE_IMPORTED = "databaseimported"
        private const val PREF_CACHE_MAX = "cachemax"
        private const val PREF_HAS_SHOWN_GO_YOUTUBE = "hasshowngoyoutube"
        private const val PREF_NEWS_CACHING_WIFI_ONLY = "news_caching_wifi_only"
        private const val PREF_LAST_EXIT_TAB = "last_exit_tab"
        private const val DEMO_ALERT = "DEMO_ALERT"
    }
}