package im.dacer.kata.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import im.dacer.kata.data.model.bigbang.ContextStr
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.room.dao.ContextStrDao
import im.dacer.kata.data.room.dao.WordDao


@Database(entities = [Word::class, ContextStr::class], version = 1, exportSchema = false)
abstract class WordAppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun contextStrDao(): ContextStrDao
}

