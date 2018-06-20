package im.dacer.kata.data.room

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

object NewsMigrationHelper {
    fun get(): Array<Migration> {
        return arrayOf(
        )
    }

}

object HistoryMigrationHelper {
    fun get(): Array<Migration> {
        return arrayOf(
                object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {}
                }
        )
    }

}