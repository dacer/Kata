package im.dacer.kata.util.action

import android.net.Uri

class JishoSearchAction : WebSearchAction() {

    override fun createSearchUriWithEncodedText(encodedText: String?): Uri {
        return Uri.parse("http://jisho.org/start/" + encodedText!!)
    }

    companion object {

        fun create(): JishoSearchAction {
            return JishoSearchAction()
        }
    }
}
