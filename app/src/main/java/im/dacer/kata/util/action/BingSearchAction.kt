package im.dacer.kata.util.action

import android.net.Uri

class BingSearchAction : WebSearchAction() {

    override fun createSearchUriWithEncodedText(encodedText: String?): Uri {
        return Uri.parse("https://www.bing.com/start?q=" + encodedText!!)
    }

    companion object {

        fun create(): BingSearchAction {
            return BingSearchAction()
        }
    }
}
