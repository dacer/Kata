package im.dacer.kata.injection.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import im.dacer.kata.data.room.HistoryMigrationHelper
import im.dacer.kata.data.room.NewsMigrationHelper
import im.dacer.kata.data.room.WordMigrationHelper
import im.dacer.kata.data.room.dao.*
import im.dacer.kata.data.room.database.HistoryAppDatabase
import im.dacer.kata.data.room.database.NewsAppDatabase
import im.dacer.kata.data.room.database.WordAppDatabase
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
                    .addMigrations(*NewsMigrationHelper.get())
                    .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun providesHistoryAppDatabase(@ApplicationContext context: Context): HistoryAppDatabase =
            Room.databaseBuilder(context, HistoryAppDatabase::class.java, "History")
                    .addMigrations(*HistoryMigrationHelper.get())
                    .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun providesWordAppDatabase(@ApplicationContext context: Context): WordAppDatabase =
            Room.databaseBuilder(context, WordAppDatabase::class.java, "Word")
                    .addMigrations(*WordMigrationHelper.get())
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

    @Provides
    @Singleton
    fun providesWordDao(database: WordAppDatabase): WordDao = database.wordDao()

    @Provides
    @Singleton
    fun providesContextStrDao(database: WordAppDatabase): ContextStrDao = database.contextStrDao()

}