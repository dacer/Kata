package im.dacer.kata.data.room

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

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

object WordMigrationHelper {
    fun get(): Array<Migration> {
        return arrayOf(
        )
    }

}