package im.dacer.kata.util.engine

import android.content.Context
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.util.action.*

object SearchEngine {

    const val GOOGLE = "Google"
    const val BING = "Bing"
    const val DUCKDUCKGO = "DuckDuckGo"
    const val JISHO = "Jisho"
    const val MOJI = "Moji"

    val supportSearchEngineList: Array<String>
        get() = arrayOf(GOOGLE, BING, DUCKDUCKGO, JISHO, MOJI)

    fun getDefaultSearchAction(context: Context): Action? {
        val multiprocessPref = MultiprocessPref(context)
        return getSearchAction(multiprocessPref.searchEngine)
    }

    fun getSearchAction(engine: String): Action? {
        when (engine) {
            GOOGLE -> return GoogleSearchAction.create()
            BING -> return BingSearchAction.create()
            DUCKDUCKGO -> return DuckDuckGoSearchAction.create()
            JISHO -> return JishoSearchAction.create()
            MOJI -> return MojiSearchAction.create()
        }
        return null
    }
}
