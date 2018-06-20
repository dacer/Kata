package im.dacer.kata.data.room.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import im.dacer.kata.data.model.bigbang.History
import im.dacer.kata.data.room.dao.HistoryDao


@Database(entities = [(History::class)], version = 2, exportSchema = false)
abstract class HistoryAppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}

