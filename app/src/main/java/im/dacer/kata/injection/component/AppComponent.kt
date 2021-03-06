package im.dacer.kata.injection.component

import android.app.Application
import android.content.Context
import dagger.Component
import im.dacer.kata.data.NewsDataManager
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.PreferencesHelper
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.room.dao.*
import im.dacer.kata.data.room.database.NewsAppDatabase
import im.dacer.kata.injection.module.AppModule
import im.dacer.kata.injection.qualifier.ApplicationContext
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

    fun appDatabase(): NewsAppDatabase

    fun easyNewsDao(): EasyNewsDao

    fun nhkNewsDao(): NhkNewsDao

    fun historyDao(): HistoryDao

    fun wordDao(): WordDao

    fun contextStrdDao(): ContextStrDao
}
