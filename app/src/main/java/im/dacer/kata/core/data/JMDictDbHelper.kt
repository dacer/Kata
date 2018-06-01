package im.dacer.kata.core.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Dacer on 10/01/2018.
 */

open class JMDictDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    protected val dbFile = context.getDatabasePath(DB_NAME)!!

    val isDataBaseExists: Boolean
        get() = dbFile.exists()

    override fun onCreate(arg0: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        val DB_NAME = "JMDict"
        val DB_VERSION = 1
    }
}