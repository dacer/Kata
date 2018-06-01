package im.dacer.kata.core.data

import android.content.Context
import im.dacer.kata.SearchEngine
import im.dacer.kata.core.model.BigBangStyle
import im.dacer.kata.core.util.LangUtils
import im.dacer.kata.core.util.WebParser
import net.grandcentrix.tray.TrayPreferences

/**
 * Created by Dacer on 15/01/2018.
 */
class MultiprocessPref(context: Context): TrayPreferences(context, "Kata", 1) {


    fun getLineSpace(): Int = bigBangStyle.lineSpace

    fun getItemSpace(): Int = bigBangStyle.itemSpace

    fun getItemTextSize(): Int = bigBangStyle.textSize

    fun getFuriganaItemTextSize(): Int = bigBangStyle.furiganaTextSize


    var bigBangStyle: BigBangStyle
        get() = BigBangStyle.getFrom(getString(BIG_BANG_STYLE, ""))
        set(value) { put(BIG_BANG_STYLE, value.toReadableString()) }

    var searchEngine: String
        get() = getString(SEARCH_ENGINE, SearchEngine.GOOGLE)!!
        set(value) { put(SEARCH_ENGINE, value) }


    var hideFurigana: Boolean
        get() = getBoolean(HIDE_FURIGANA, false)
        set(value) { put(HIDE_FURIGANA, value) }

    var showFloatDialog: Boolean
        get() = getBoolean(SHOW_FLOAT_DIALOG, true)
        set(value) { put(SHOW_FLOAT_DIALOG, value) }

    var targetLang: String
        get() = getString(TARGET_LANG, LangUtils.DEFAULT_TARGET_LANG_KEY)!!
        set(value) { put(TARGET_LANG, value) }

    var webParser: WebParser.Parser
        get() = WebParser.getParseBy(getString(WEB_PARSER, WebParser.DEFAULT_PARSER.name)!!)
        set(value) { put(WEB_PARSER, value.name) }

    var useWebParser: Boolean = false
        get() = webParser != WebParser.Parser.DO_NOT_USE

    var enhancedMode: Boolean
        get() = getBoolean(ENHANCED_MODE, false)
        set(value) { put(ENHANCED_MODE, value) }

    var tutorialFinished: Boolean
        get() = getBoolean(TUTORIAL_FINISHED, false)
        set(value) { put(TUTORIAL_FINISHED, value) }

    companion object {
        private const val BIG_BANG_STYLE = "pref_big_bang_style"
        private const val SEARCH_ENGINE = "pref_search_engine"
        private const val HIDE_FURIGANA = "pref_hide_furigana"
        private const val SHOW_FLOAT_DIALOG = "pref_show_float_dialog"
        private const val TARGET_LANG = "pref_target_lang"
        private const val WEB_PARSER = "pref_web_parser"
        private const val ENHANCED_MODE = "pref_enhanced_mode"
        private const val TUTORIAL_FINISHED = "pref_tutorial_finished"

    }

}