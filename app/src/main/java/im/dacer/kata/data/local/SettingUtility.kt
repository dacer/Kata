package im.dacer.kata.data.local

import im.dacer.kata.ui.main.MainPresenter
import im.dacer.kata.util.helper.AnkiDroidHelper.Companion.ANKI_MODEL_NAME
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
        get() = mHelper[PREF_CACHE_MAX, 999]
        set(value) { mHelper[PREF_CACHE_MAX] = value }

    var hasShownGoYoutube : Boolean
        get() = mHelper[PREF_HAS_SHOWN_GO_YOUTUBE, false]
        set(value) { mHelper[PREF_HAS_SHOWN_GO_YOUTUBE] = value }

    var lastExitTab : Long
        get() = mHelper[PREF_LAST_EXIT_TAB, MainPresenter.DrawerItem.INBOX.id]
        set(value) { mHelper[PREF_LAST_EXIT_TAB] = value }

    var ankiDeckId : Long
        get() = mHelper[PREF_ANKI_DECK_ID, -1L]
        set(value) { mHelper[PREF_ANKI_DECK_ID] = value }

    var ankiModelId : Long
        get() = mHelper[ANKI_MODEL_NAME, -1L]
        set(value) { mHelper[ANKI_MODEL_NAME] = value }

    var moveToMasteredAfterExport : Boolean
        get() = mHelper[PREF_MOVE_TO_MASTERED_AFTER_EXPORT, true]
        set(value) { mHelper[PREF_MOVE_TO_MASTERED_AFTER_EXPORT] = value }

    var androidQAlertShowed : Boolean
        get() = mHelper[PREF_ANDROID_Q_ALERT_SHOWED, false]
        set(value) { mHelper[PREF_ANDROID_Q_ALERT_SHOWED] = value }

    companion object {
        private const val PREF_LISTEN_CLIPBOARD = "listenclipboard"
        private const val PREF_DATABASE_IMPORTED = "databaseimported"
        private const val PREF_CACHE_MAX = "cachemax"
        private const val PREF_HAS_SHOWN_GO_YOUTUBE = "hasshowngoyoutube"
        private const val PREF_LAST_EXIT_TAB = "last_exit_tab"
        private const val PREF_ANKI_DECK_ID = "PREF_ANKI_DECK_ID"
        private const val PREF_MOVE_TO_MASTERED_AFTER_EXPORT = "PREF_MOVE_TO_MASTERED_AFTER_EXPORT"
        private const val PREF_ANDROID_Q_ALERT_SHOWED = "PREF_ANDROID_Q_ALERT_SHOWED"
    }
}