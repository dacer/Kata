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
    private const val URL_PATTERN = "^http(|s):\\/\\/www3\\.nhk\\.or\\.jp\\/news\\/html\\/.+\\/.+\\.html\$"


    class ContentNotFound: Exception("Content not found")

    fun checkUrlAvailable(url: String) : Boolean {
        return url.matches(Regex(URL_PATTERN))
    }

    fun fetchContent(targetUrl: String): Observable<String> {
        return Observable.fromCallable {
            Timber.e("parser $targetUrl")
            try {
                val doc = Jsoup.parse(URL(targetUrl), 20000)

                doc.outputSettings(Document.OutputSettings().prettyPrint(false))   //makes html() preserve linebreaks and spacing

                //v1
                val article = doc.select(".news_add div").first()
                if (article != null) {
                    return@fromCallable article.outputElement()
                }

                //v2
                val newsBody = doc.select("#news_textbody").first()
                val newsBodyMore = doc.select("#news_textmore").first()
                if (newsBody != null && newsBodyMore != null) {

                    return@fromCallable "${newsBody.outputElement()}\n${newsBodyMore.outputElement()}"
                }

            } catch (e: Throwable) {
            }

            throw ContentNotFound()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun Element.outputElement() : String {
        this.select("br").append("\\n")
        this.select("p").prepend("\\n\\n")
        val s = this.html().replace("\\n", "\n")
        return Jsoup.clean(s, "", Whitelist.none(), Document.OutputSettings().prettyPrint(false))

    }
}