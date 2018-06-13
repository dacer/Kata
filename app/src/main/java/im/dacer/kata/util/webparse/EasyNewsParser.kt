package im.dacer.kata.util.webparse

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.safety.Whitelist
import timber.log.Timber
import java.net.URL

object EasyNewsParser {
    private const val URL_PATTERN = "^http(|s)://www3\\.nhk\\.or\\.jp/news/easy/.+/.+\\.html\$"


    class ContentNotFound: Exception("Content not found")

    fun checkUrlAvailable(url: String) : Boolean {
        return url.matches(Regex(URL_PATTERN))
    }

    fun fetchContent(targetUrl: String): Observable<String> {
        return Observable.fromCallable {
            try {
                val doc = Jsoup.parse(URL(targetUrl), 20000)
                doc.outputSettings(Document.OutputSettings().prettyPrint(false))   //makes html() preserve linebreaks and spacing

                val article = doc.select("#js-article-body").first()
                if (article != null) {
                    return@fromCallable article.outputElement()
                }

            } catch (e: Throwable) {
            }

            return@fromCallable ""
//            throw ContentNotFound()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun Element.outputElement() : String {
        this.select("br").append("\\n\\n")
        this.select("p").prepend("\\n\\n")
        val s = this.html().replace("\\n", "\n")
        return Jsoup.clean(s, "", Whitelist.none(), Document.OutputSettings().prettyPrint(false))

    }
}