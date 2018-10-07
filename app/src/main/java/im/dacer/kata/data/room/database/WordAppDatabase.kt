package im.dacer.kata.data.room.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.room.dao.WordDao


@Database(entities = [(Word::class)], version = 1, exportSchema = false)
abstract class WordAppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

