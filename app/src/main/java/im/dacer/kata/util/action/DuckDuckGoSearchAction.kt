package im.dacer.kata.util.action

import android.net.Uri

class DuckDuckGoSearchAction : WebSearchAction() {

    override fun createSearchUriWithEncodedText(encodedText: String?): Uri {
        return Uri.parse("https://duckduckgo.com/?q=" + encodedText!!)
    }

    companion object {
        fun create(): DuckDuckGoSearchAction {
            return DuckDuckGoSearchAction()
        }
    }
}
