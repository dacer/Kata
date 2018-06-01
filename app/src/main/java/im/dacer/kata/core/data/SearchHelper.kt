package im.dacer.kata.core.data

import android.database.sqlite.SQLiteDatabase
import im.dacer.kata.core.model.BigbangSearchResult
import im.dacer.kata.core.model.DictEntry
import im.dacer.kata.core.model.DictKanji
import im.dacer.kata.core.model.DictReading

/**
 * Created by Dacer on 12/01/2018.
 */
class SearchHelper(private val db: SQLiteDatabase) {

    fun search(text: String): BigbangSearchResult {
        val isKanjiInside = kanjiInside(text)
        var dictReadingList: List<im.dacer.kata.core.model.DictReading>? = null
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

    fun searchReading(entryId: Long): List<im.dacer.kata.core.model.DictReading> {
        val result = arrayListOf<im.dacer.kata.core.model.DictReading>()
        val query = im.dacer.kata.core.model.DictReading.FACTORY.search_by_entry_id(entryId)
        db.rawQuery(query.statement, query.args).use { cursor ->
            while (cursor?.moveToNext() == true) {
                result.add(im.dacer.kata.core.model.DictReading.SELECT_ALL_MAPPER.map(cursor))
            }
        }
        return result
    }

    private fun searchKanji(kanji: String): List<Long?> {
        val result = arrayListOf<Long?>()
        val query = im.dacer.kata.core.model.DictKanji.FACTORY.search(kanji)
        db.rawQuery(query.statement, query.args).use { cursor ->
            while (cursor?.moveToNext() == true) {
                result.add(im.dacer.kata.core.model.DictKanji.SELECT_ALL_MAPPER.map(cursor).id_in_entry())
            }
        }
        return result
    }

    private fun searchReading(reading: String): List<Long?> {
        val result = arrayListOf<Long?>()
        val query = im.dacer.kata.core.model.DictReading.FACTORY.search(reading)
        db.rawQuery(query.statement, query.args).use { cursor ->
            while (cursor?.moveToNext() == true) {
                result.add(im.dacer.kata.core.model.DictReading.SELECT_ALL_MAPPER.map(cursor).id_in_entry())
            }
        }
        return result
    }

    private fun searchEntry(id: Long): im.dacer.kata.core.model.DictEntry? {
        val query = im.dacer.kata.core.model.DictEntry.FACTORY.search(id)
        db.rawQuery(query.statement, query.args).use { cursor ->
            while (cursor?.moveToNext() == true) {
                return im.dacer.kata.core.model.DictEntry.SELECT_ALL_MAPPER.map(cursor)
            }
        }
        return null
    }


    private fun kanjiInside(text: String): Boolean {
        return text.matches(Regex(".*[\\u4e00-\\u9faf]+.*"))
    }

}