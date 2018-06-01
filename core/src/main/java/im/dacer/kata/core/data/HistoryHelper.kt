package im.dacer.kata.core.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import im.dacer.kata.core.model.History
import im.dacer.kata.core.model.HistoryModel
import im.dacer.kata.segment.util.LogUtils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Dacer on 13/02/2018.
 */
class HistoryHelper {

    companion object {

        fun saveAsync(context: Context, string: String, alias: String = "") {
            Observable.fromCallable {
                val historyDb = HistoryDbHelper(context).writableDatabase
                HistoryHelper.save(historyDb, string, alias)
                historyDb.close()
            }.subscribeOn(Schedulers.io()).subscribe({}, { LogUtils.log(it, context) })
        }

        fun save(db: SQLiteDatabase, text: String, alias: String = "") {
            val insertRow = HistoryModel.Insert_row(db)
            insertRow.bind(text, alias, false, System.currentTimeMillis())
            try {
                insertRow.program.executeInsert()
            } catch (e: Exception) {
                throw e
            }
        }

        fun delete(db: SQLiteDatabase, id: Long) {
            val deleteRow = HistoryModel.Delete_row(db)
            deleteRow.bind(id)
            try {
                deleteRow.program.executeUpdateDelete()
            } catch (e: Exception) {
                throw e
            }
        }

        fun update(db: SQLiteDatabase, id: Long, alias: String?, star: Boolean?) {
            val updateRow = HistoryModel.Update_alias_star(db)
            updateRow.bind(alias ?: "", star == true, id)
            try {
                updateRow.program.executeUpdateDelete()
            } catch (e: Exception) {
                throw e
            }
        }

        fun get(db: SQLiteDatabase, limit: Int): List<History> {
            val result = arrayListOf<History>()
            val queryStarred = History.FACTORY.select_all_starred()
            db.rawQuery(queryStarred.statement, queryStarred.args).use { cursor ->
                while (cursor?.moveToNext() == true) {
                    result.add(History.SELECT_ALL_MAPPER.map(cursor))
                }
            }
            val query = History.FACTORY.select_unstarred_limit(limit.toLong())
            db.rawQuery(query.statement, query.args).use { cursor ->
                while (cursor?.moveToNext() == true) {
                    result.add(History.SELECT_ALL_MAPPER.map(cursor))
                }
            }
            return result
        }
    }

}