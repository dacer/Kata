package im.dacer.kata.util.webparse

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.safety.Whitelist

abstract class BaseParser {
    class ContentNotFound: RuntimeException("Content not found")

    abstract fun checkUrlAvailable(url: String) : Boolean

    protected fun Element.removeFurigana(): Element {
        this.select("rt").remove()
        return this
    }

    protected fun Element.outputElement() : String {
        this.select("br").append("\\n\\n")
        this.select("p").prepend("\\n\\n")
        val s = this.html().replace("\\n", "\n")
        return Jsoup.clean(s, "", Whitelist.none(), Document.OutputSettings().prettyPrint(false))

    }
}