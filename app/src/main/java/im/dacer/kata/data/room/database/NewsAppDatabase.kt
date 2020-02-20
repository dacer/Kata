package im.dacer.kata.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import im.dacer.kata.data.model.news.EasyNews
import im.dacer.kata.data.model.news.NhkNews
import im.dacer.kata.data.room.dao.NhkNewsDao
import im.dacer.kata.data.room.dao.EasyNewsDao


@Database(entities = [(EasyNews::class), (NhkNews::class)], version = 1, exportSchema = false)
abstract class NewsAppDatabase : RoomDatabase() {
    abstract fun easyNewsDao(): EasyNewsDao
    abstract fun nhkNewsDao(): NhkNewsDao
}

