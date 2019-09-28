package im.dacer.kata.data.local


import android.content.Context
import android.content.SharedPreferences
import im.dacer.kata.injection.qualifier.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesHelper @Inject
constructor(@ApplicationContext context: Context) {

    private val mPref: SharedPreferences

    init {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun clear() {
        mPref.edit().clear().apply()
    }

    operator fun get(key: String, defaultInt: Int): Int {
        return mPref.getInt(key, defaultInt)
    }

    operator fun get(key: String, defaultLong: Long): Long {
        return mPref.getLong(key, defaultLong)
    }

    operator fun get(key: String, defaultBoolean: Boolean): Boolean {
        return mPref.getBoolean(key, defaultBoolean)
    }

    operator fun get(key: String, defaultString2: String): String {
        return mPref.getString(key, defaultString2)!!
    }

    operator fun set(key: String, value: Int) {
        mPref.edit().putInt(key, value).apply()
    }

    operator fun set(key: String, value: Long) {
        mPref.edit().putLong(key, value).apply()
    }

    operator fun set(key: String, value: Boolean) {
        mPref.edit().putBoolean(key, value).apply()
    }

    operator fun set(key: String, value: String) {
        mPref.edit().putString(key, value).apply()
    }

    companion object {
        val PREF_FILE_NAME = "config"
    }

}