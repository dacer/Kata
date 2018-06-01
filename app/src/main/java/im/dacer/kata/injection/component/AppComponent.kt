package im.dacer.kata.injection.component

import android.app.Application
import android.content.Context
import dagger.Component
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.room.AppDatabase
import im.dacer.kata.data.room.NewsDao
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.module.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @ApplicationContext
    fun context(): Context

    fun application(): Application

    fun newsDataManager(): NewsDataManager

    fun appDatabase(): AppDatabase

    fun newsDao(): NewsDao
}
