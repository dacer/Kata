package im.dacer.kata.data.local

import android.content.Context
import android.graphics.Color
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.BigBangStyle
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.engine.SearchEngine
import im.dacer.kata.util.segment.Parser
import im.dacer.kata.util.segment.parser.ApiParser
import im.dacer.kata.util.segment.parser.KuromojiParser
import im.dacer.kata.util.webparse.WebParser
import net.grandcentrix.tray.TrayPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dacer on 15/01/2018.
 */

@Singleton
class MultiprocessPref @Inject constructor(@ApplicationContext context: Context): TrayPreferences(context, "Kata", 1) {

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

    var newsCachingWifiOnly : Boolean
        get() = getBoolean(PREF_NEWS_CACHING_WIFI_ONLY, true)
        set(value) { put(PREF_NEWS_CACHING_WIFI_ONLY, value) }

    var easterEgg : Boolean
        get() = getBoolean(EASTER_EGG, false)
        set(value) { put(EASTER_EGG, value) }

    var showPicWifiOnly : Boolean
        get() = getBoolean(PREF_SHOW_PIC_WIFI_ONLY, false)
        set(value) { put(PREF_SHOW_PIC_WIFI_ONLY, value) }

    var backgroundColor : Int
        get() = getInt(PREF_BACKGROUND_COLOR, Color.WHITE)
        set(value) { put(PREF_BACKGROUND_COLOR, value) }

    var hasShownWordBookTips : Boolean
        get() = getBoolean(HAS_SHOWN_WORD_BOOK_TIPS, false)
        set(value) { put(HAS_SHOWN_WORD_BOOK_TIPS, value) }

    var enableWordBook : Boolean
        get() = getBoolean(PREF_ENABLE_WORD_BOOK, true)
        set(value) { put(PREF_ENABLE_WORD_BOOK, value) }

    var analyzeUrlInClipboard : Boolean
        get() = getBoolean(PREF_ANALYZE_URL_IN_CLIPBOARD, false)
        set(value) { put(PREF_ANALYZE_URL_IN_CLIPBOARD, value) }

    var segmentParserValue : Int
        get() = getInt(PREF_SEGMENT_PARSER, SegmentParser.KUROMOJI_LOCAL.value)
        set(value) { put(PREF_SEGMENT_PARSER, value) }

    var segmentParserEnum : SegmentParser
        get() {
            return when (segmentParserValue) {
                SegmentParser.KUROMOJI_LOCAL.value -> SegmentParser.KUROMOJI_LOCAL
                SegmentParser.KUROMOJI_ONLINE.value -> SegmentParser.KUROMOJI_ONLINE
                else -> SegmentParser.KUROMOJI_ONLINE
            }
        }
        set(parser){
            put(PREF_SEGMENT_PARSER, parser.value)
        }

    val segmentParser : Parser<List<KanjiResult>>
        get() {
            return when (segmentParserValue) {
                SegmentParser.KUROMOJI_LOCAL.value -> KuromojiParser()
                SegmentParser.KUROMOJI_ONLINE.value -> ApiParser()
                else -> KuromojiParser()
            }
        }

    fun getSegmentParserNameList(c: Context): List<String> {
        return SegmentParser.values().map { c.getString(it.nameResId) }
    }

    companion object {
        private const val BIG_BANG_STYLE = "pref_big_bang_style"
        private const val SEARCH_ENGINE = "pref_search_engine"
        private const val HIDE_FURIGANA = "pref_hide_furigana"
        private const val SHOW_FLOAT_DIALOG = "pref_show_float_dialog"
        private const val TARGET_LANG = "pref_target_lang"
        private const val WEB_PARSER = "pref_web_parser"
        private const val ENHANCED_MODE = "pref_enhanced_mode"
        private const val TUTORIAL_FINISHED = "pref_tutorial_finished"
        private const val PREF_NEWS_CACHING_WIFI_ONLY = "news_caching_wifi_only"
        private const val PREF_SHOW_PIC_WIFI_ONLY = "show_pic_wifi_only"
        private const val PREF_BACKGROUND_COLOR = "background_color"
        private const val HAS_SHOWN_WORD_BOOK_TIPS = "HAS_SHOWN_WORD_BOOK_TIPS"
        private const val PREF_ENABLE_WORD_BOOK = "PREF_ENABLE_WORD_BOOK"
        private const val PREF_ANALYZE_URL_IN_CLIPBOARD = "PREF_ANALYZE_URL_IN_CLIPBOARD"
        private const val PREF_SEGMENT_PARSER = "PREF_SEGMENT_PARSER"

        private const val EASTER_EGG = "EASTER_EGG"

        enum class SegmentParser(val value: Int, val nameResId: Int) {
            KUROMOJI_LOCAL(0, R.string.text_analysis_engine_basic),
            KUROMOJI_ONLINE(1, R.string.text_analysis_engine_Online)
        }

    }

}