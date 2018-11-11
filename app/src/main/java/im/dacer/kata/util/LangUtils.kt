package im.dacer.kata.util

import com.rx2androidnetworking.Rx2AndroidNetworking
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.model.bigbang.generated.autovalue.DictEntry
import im.dacer.kata.util.extension.urlEncode
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dacer on 03/02/2018.
 * https://ctrlq.org/code/19909-google-translate-apicc
 * Thanks Google
 */
@Singleton
class LangUtils @Inject constructor(private val appPre: MultiprocessPref) {

    fun fetchTranslation(dictEntry: DictEntry): Observable<String> {
        val targetLang = getTargetLang()

        //since people in China cannot use Google..
        if (targetLang == LANG_CHINESE_KEY) {
            return Observable.just(dictEntry.gloss_cn())
        }

        if (targetLang == DEFAULT_TARGET_LANG_KEY) {
            return Observable.just(dictEntry.gloss())
        }

        return translateOnline(dictEntry.gloss()!!, targetLang)
                .map { return@map it
                        .replace("，", ", ")
                        .split(", ")
                        .distinct()
                        .joinToString(", ") }
    }

    fun translateOnline(sourceStr: String, fromLangLang: String = LANG_ENGLISH_KEY, targetLang: String = getTargetLang()): Observable<String> {
        return Rx2AndroidNetworking.get(getTranslationUrl(fromLangLang, targetLang, sourceStr))
                .build()
                .stringObservable
                .map {
                    val array = JSONArray(it)
                    return@map array.getJSONArray(0).getJSONArray(0).getString(0)
                }
                .subscribeOn(Schedulers.io())
    }

    private fun getTargetLang(): String {
        var targetLang = appPre.targetLang
        if (!LANG_KEY_LIST.contains(targetLang)) targetLang = LANG_ENGLISH_KEY
        return targetLang
    }

    companion object {
        const val LANG_ENGLISH_KEY = "en"
        const val LANG_CHINESE_KEY = "zh-CN"
        const val LANG_JAPANESE_KEY = "ja"
        private const val LANG_ENGLISH_VALUE = "English"

        private fun getTranslationUrl(fromLang: String, targetLang: String, sourceStr: String) =
                "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$fromLang&tl=$targetLang&dt=t&ie=UTF-8&oe=UTF-8&q=${sourceStr.urlEncode()}"

        private fun isZhCN(): Boolean {
            return Locale.getDefault().language == "zh" && Locale.getDefault().country == "CN"

        }

        fun getLangByKey(key: String): String {
            val index = LANG_KEY_LIST.toList().indexOf(key)
            return if (index == -1 || index > LANG_LIST.size) {
                LANG_ENGLISH_VALUE
            } else {
                LANG_LIST[index]
            }
        }
        val LANG_LIST = arrayOf(LANG_ENGLISH_VALUE, "Afrikaans", "Albanian", "Arabic", "Azerbaijani", "Basque", "Belarusian", "Bengali", "Bulgarian", "Catalan", "Chinese Simplified", "Chinese Traditional ", "Croatian", "Czech", "Danish", "Dutch", "Esperanto", "Estonian", "Filipino", "Finnish", "French", "Galician", "Georgian", "German", "Greek", "Gujarati", "Haitian Creole", "Hebrew", "Hindi", "Hungarian", "Icelandic", "Indonesian", "Irish", "Italian", "Japanese", "Kannada", "Korean", "Latin", "Latvian", "Lithuanian", "Macedonian", "Malay", "Maltese", "Norwegian", "Persian", "Polish", "Portuguese", "Romanian", "Russian", "Serbian", "Slovak", "Slovenian", "Spanish", "Swahili", "Swedish", "Tamil", "Telugu", "Thai", "Turkish", "Ukrainian", "Urdu", "Vietnamese", "Welsh", "Yiddish")
        val LANG_KEY_LIST = arrayOf(LANG_ENGLISH_KEY, "af", "sq", "ar", "az", "eu", "be", "bn", "bg", "ca", "zh-CN", "zh-TW", "hr", "cs", "da", "nl", "eo", "et", "tl", "fi", "fr", "gl", "ka", "de", "el", "gu", "ht", "iw", "hi", "hu", "is", "id", "ga", "it", "ja", "kn", "ko", "la", "lv", "lt", "mk", "ms", "mt", "no", "fa", "pl", "pt", "ro", "ru", "sr", "sk", "sl", "es", "sw", "sv", "ta", "te", "th", "tr", "uk", "ur", "vi", "cy", "yi")

        var DEFAULT_TARGET_LANG_KEY: String = LANG_ENGLISH_KEY
            get() = if (isZhCN()) LANG_CHINESE_KEY else LANG_ENGLISH_KEY
    }
}