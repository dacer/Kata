package im.dacer.kata.util.action

import android.net.Uri

class GoogleSearchAction : WebSearchAction() {

    override fun createSearchUriWithEncodedText(encodedText: String?): Uri {
        return Uri.parse("https://www.google.com/start?q=" + encodedText!!)
    }

    companion object {

        fun create(): GoogleSearchAction {
            return GoogleSearchAction()
        }
    }
}
