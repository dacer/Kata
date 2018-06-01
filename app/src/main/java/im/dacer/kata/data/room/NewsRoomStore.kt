package im.dacer.kata.data.room

import android.content.Context
import android.arch.persistence.room.Room

class NewsRoomStore(context: Context) {
    val db = Room.databaseBuilder(context, AppDatabase::class.java, "news").build()

}
