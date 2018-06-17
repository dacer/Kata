package im.dacer.kata.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import im.dacer.kata.data.model.bigbang.History


@Database(entities = [(History::class)], version = 2, exportSchema = false)
abstract class HistoryAppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}

