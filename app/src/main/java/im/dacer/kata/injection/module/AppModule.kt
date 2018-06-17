package im.dacer.kata.injection.module

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import android.content.Context
import dagger.Module
import dagger.Provides
import im.dacer.kata.data.room.*
import im.dacer.kata.injection.qualifier.ApplicationContext
import javax.inject.Singleton


@Module
class AppModule(private val application: Application) {

    @Provides
    internal fun provideApplication(): Application = application

    @Provides
    @ApplicationContext
    internal fun provideContext(): Context = application

    @Provides
    @Singleton
    fun providesNewsAppDatabase(@ApplicationContext context: Context): NewsAppDatabase =
            Room.databaseBuilder(context, NewsAppDatabase::class.java, "news")
                    .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun providesHistoryAppDatabase(@ApplicationContext context: Context): HistoryAppDatabase =
            Room.databaseBuilder(context, HistoryAppDatabase::class.java, "History")
                    .addMigrations(object : Migration(1, 2){
                        override fun migrate(database: SupportSQLiteDatabase) {}
                    })
                    .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun providesEasyNewsDao(database: NewsAppDatabase): EasyNewsDao = database.easyNewsDao()

    @Provides
    @Singleton
    fun providesNhkNewsDao(database: NewsAppDatabase): NhkNewsDao = database.nhkNewsDao()


    @Provides
    @Singleton
    fun providesHistoryDao(database: HistoryAppDatabase): HistoryDao = database.historyDao()



    companion object {
    }
}