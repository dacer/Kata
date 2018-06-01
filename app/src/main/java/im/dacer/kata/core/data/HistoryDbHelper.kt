package im.dacer.kata.core.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import im.dacer.kata.core.model.HistoryModel

/**
 * Created by Dacer on 13/02/2018.
 */

open class HistoryDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("DROP TABLE IF EXISTS ${HistoryModel.TABLE_NAME}")
            db.execSQL(HistoryModel.CREATE_TABLE)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        val DB_NAME = "History"
        val DB_VERSION = 1
    }
}