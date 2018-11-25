package im.dacer.kata.data.local

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.BigbangSearchResult
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.model.bigbang.generated.autovalue.DictEntry
import im.dacer.kata.data.model.bigbang.generated.autovalue.DictKanji
import im.dacer.kata.data.model.bigbang.generated.autovalue.DictReading
import im.dacer.kata.data.model.segment.CombinedResult
import im.dacer.kata.data.room.dao.ContextStrDao
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.ui.flashcard.FlashcardAdapter
import im.dacer.kata.util.LangUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dacer on 12/01/2018.
 *
 * remember to call onDestroy() !!
 */
@Singleton
class SearchDictHelper @Inject constructor(@ApplicationContext val context: Context) {
    private val db = JMDictDbHelper(context).readableDatabase

    fun onDestroy() {
//        db.close()
    }

    /**
     * the CombineResult will not contain contextStr
     */
    fun searchForCombineResultAndTranslateIfNoMeaning(strForSearch: String, langUtils: LangUtils): Observable<CombinedResult> {
        return Observable.fromCallable{ search(strForSearch) }
                .flatMap {
                    Observable.zip(
                            dealWithDictEntryList(it.dictEntryList, langUtils),
                            dealWithDictReadingList(it.dictReadingList),
                            BiFunction<String, String, CombinedResult> {
                                meaningStr, readingStr -> CombinedResult(strForSearch, meaningStr, readingStr, null)
                            }
                    )
                }
                .flatMap { translateDirectlyIfNoMeaning(strForSearch, it, langUtils) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun searchForCombineResultFull(word: Word, langUtils: LangUtils, contextStrDao: ContextStrDao): Observable<CombinedResult> {
        val strForSearch = word.baseForm
        return Observable.fromCallable{ search(strForSearch) }
                .flatMap {
                    Observable.zip(
                            dealWithDictEntryList(it.dictEntryList, langUtils),
                            dealWithDictReadingList(it.dictReadingList),
                            getContextStr(word, contextStrDao),
                            Function3<String, String, CharSequence, CombinedResult> {
                                meaningStr, readingStr, contextStr ->
                                CombinedResult(strForSearch, meaningStr, readingStr, contextStr)
                            }
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun search(text: String): BigbangSearchResult {
        val isKanjiInside = kanjiInside(text)
        var dictReadingList: List<DictReading>? = null
        val idInEntryList = if (isKanjiInside) {
            searchKanji(text)
        } else {
            searchReading(text)
        }
        val dictEntryList = idInEntryList.mapNotNull { it?.let { it1 -> searchEntry(it1) } }
        if (isKanjiInside) {
            dictReadingList = dictEntryList.flatMap { searchReading(it.id()) }
        }
        return BigbangSearchResult(dictEntryList, dictReadingList)
    }

    private fun translateDirectlyIfNoMeaning(strForSearch: String, combinedResult: CombinedResult,
                                             langUtils: LangUtils):
            Observable<CombinedResult> {
        if (combinedResult.meaningStr.isBlank()) {
            return langUtils.translateOnline(sourceStr = strForSearch, fromLang = LangUtils.LANG_JAPANESE_KEY).map {
                combinedResult.meaningStr = "$it\n(${context.getString(R.string.translated_by_google)})"
                return@map combinedResult
            }
        }
        return Observable.just(combinedResult)
    }

    private fun searchReading(entryId: Long): List<DictReading> {
        val result = arrayListOf<DictReading>()
        val query = DictReading.FACTORY.search_by_entry_id(entryId)
        db.rawQuery(query.statement, query.args).use { cursor ->
            while (cursor?.moveToNext() == true) {
                result.add(DictReading.SELECT_ALL_MAPPER.map(cursor))
            }
        }
        return result
    }

    private fun searchKanji(kanji: String): List<Long?> {
        val result = arrayListOf<Long?>()
        val query = DictKanji.FACTORY.search(kanji)
        db.rawQuery(query.statement, query.args).use { cursor ->
            while (cursor?.moveToNext() == true) {
                result.add(DictKanji.SELECT_ALL_MAPPER.map(cursor).id_in_entry())
            }
        }
        return result
    }

    private fun searchReading(reading: String): List<Long?> {
        val result = arrayListOf<Long?>()
        val query = DictReading.FACTORY.search(reading)
        db.rawQuery(query.statement, query.args).use { cursor ->
            while (cursor?.moveToNext() == true) {
                result.add(DictReading.SELECT_ALL_MAPPER.map(cursor).id_in_entry())
            }
        }
        return result
    }

    private fun searchEntry(id: Long): DictEntry? {
        val query = DictEntry.FACTORY.search(id)
        db.rawQuery(query.statement, query.args).use { cursor ->
            while (cursor?.moveToNext() == true) {
                return DictEntry.SELECT_ALL_MAPPER.map(cursor)
            }
        }
        return null
    }


    private fun kanjiInside(text: String): Boolean {
        return text.matches(Regex(".*[\\u4e00-\\u9faf]+.*"))
    }


    private fun dealWithDictEntryList(dictEntryList: List<DictEntry>, langUtils: LangUtils): Observable<String> {
        return Observable.fromIterable(dictEntryList)
                .flatMap { langUtils.fetchTranslation(it) }
                .toList()
                .map { meaningList -> meaningList.joinToString("\n\n") { "· $it" } }
                .toObservable()
    }

    private fun dealWithDictReadingList(dictReadingList: List<DictReading>?): Observable<String> {
        return Observable.fromCallable { dictReadingList?.joinToString(", ")
        {it.reading() ?: ""} ?: "" }
    }

    fun getContextStr(word: Word, contextStrDao: ContextStrDao): Observable<CharSequence> {
        return contextStrDao.findByWordId(word.id)
                .map { contextList ->
                    val spannableStrBuilderList = arrayListOf<SpannableStringBuilder>()
                    contextList.forEach {
                        spannableStrBuilderList.add(SpannableStringBuilder(FlashcardAdapter.CIRCLE_SYMBOL))
                        val contextStrBuilder = SpannableStringBuilder(it.text)
                        contextStrBuilder.setSpan(StyleSpan(Typeface.BOLD), it.fromIndex, it.toIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannableStrBuilderList.add(contextStrBuilder)
                        spannableStrBuilderList.add(SpannableStringBuilder("\n\n"))
                    }
                    return@map TextUtils.concat(*spannableStrBuilderList.toTypedArray())
                }
                .toObservable()
    }
}