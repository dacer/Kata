package im.dacer.kata.util.webparse

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

object EasyNewsParser: BaseParser() {
    private const val URL_PATTERN = "^http(|s)://www3\\.nhk\\.or\\.jp/news/easy/.+/.+\\.html\$"

    override fun checkUrlAvailable(url: String): Boolean {
        return url.matches(Regex(URL_PATTERN))
    }

    fun fetchContent(targetUrl: String): Observable<String> {
        return Observable.fromCallable {
            try {
                val doc = Jsoup.parse(URL(targetUrl), 20000)
                doc.outputSettings(Document.OutputSettings().prettyPrint(false))   //makes html() preserve linebreaks and spacing

                val article = doc.select("#js-article-body").first()
                if (article != null) {
                    return@fromCallable article.removeFurigana().outputElement()
                }

            } catch (e: Throwable) {}
            throw ContentNotFound()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}