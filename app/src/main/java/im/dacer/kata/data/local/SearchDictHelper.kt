package im.dacer.kata.data.local

import android.content.Context
import im.dacer.kata.data.model.bigbang.BigbangSearchResult
import im.dacer.kata.data.model.bigbang.generated.autovalue.DictEntry
import im.dacer.kata.data.model.bigbang.generated.autovalue.DictKanji
import im.dacer.kata.data.model.bigbang.generated.autovalue.DictReading
import im.dacer.kata.injection.qualifier.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Dacer on 12/01/2018.
 *
 * remember to call onDestroy() !!
 */
@Singleton
class SearchDictHelper @Inject constructor(@ApplicationContext context: Context) {
    private val db = JMDictDbHelper(context).readableDatabase

    fun onDestroy() {
//        db.close()
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

    fun searchReading(entryId: Long): List<DictReading> {
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

}