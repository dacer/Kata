package im.dacer.kata.injection.component

import android.app.Application
import android.content.Context
import dagger.Component
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.PreferencesHelper
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.room.AppDatabase
import im.dacer.kata.data.room.NewsDao
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.module.AppModule
import im.dacer.kata.util.LangUtils
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @ApplicationContext
    fun context(): Context

    fun application(): Application

    fun newsDataManager(): NewsDataManager

    fun preferenceHelper(): PreferencesHelper

    fun settingUtility(): SettingUtility

    fun searchDictHelper(): SearchDictHelper

    fun multiprocessPref(): MultiprocessPref

    fun langUtils(): LangUtils

    fun appDatabase(): AppDatabase

    fun newsDao(): NewsDao
}
