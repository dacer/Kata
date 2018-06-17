package im.dacer.kata.data.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import im.dacer.kata.data.model.news.EasyNews
import im.dacer.kata.data.model.news.NhkNews


@Database(entities = [(EasyNews::class), (NhkNews::class)], version = 1, exportSchema = false)
abstract class NewsAppDatabase : RoomDatabase() {
    abstract fun easyNewsDao(): EasyNewsDao
    abstract fun nhkNewsDao(): NhkNewsDao
}

