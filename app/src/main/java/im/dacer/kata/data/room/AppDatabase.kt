package im.dacer.kata.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import im.dacer.kata.data.model.EasyNews


@Database(entities = [(EasyNews::class)], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}

