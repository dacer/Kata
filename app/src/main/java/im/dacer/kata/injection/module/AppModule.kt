package im.dacer.kata.injection.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import im.dacer.kata.data.room.AppDatabase
import im.dacer.kata.data.room.NewsDao
import im.dacer.kata.injection.ApplicationContext
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
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "news")
                    .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun providesTaskDao(database: AppDatabase): NewsDao = database.newsDao()

    companion object {
    }
}