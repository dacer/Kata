package im.dacer.kata.injection.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import im.dacer.kata.data.room.EasyNewsDao
import im.dacer.kata.data.room.NhkNewsDao
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
    @NewsAppDatabase
    fun providesAppDatabase(@ApplicationContext context: Context): im.dacer.kata.data.room.NewsAppDatabase =
            Room.databaseBuilder(context, NewsAppDatabase::class.java, "news")
                    .allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun providesEasyNewsDao(@NewsAppDatabase database: im.dacer.kata.data.room.NewsAppDatabase): EasyNewsDao = database.easyNewsDao()

    @Provides
    @Singleton
    fun providesNhkNewsDao(@NewsAppDatabase database: im.dacer.kata.data.room.NewsAppDatabase): NhkNewsDao = database.nhkNewsDao()

    companion object {
    }
}