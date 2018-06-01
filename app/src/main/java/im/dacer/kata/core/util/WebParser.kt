package im.dacer.kata.core.util

import android.content.Context
import android.os.Build
import android.text.Html
import com.rx2androidnetworking.Rx2AndroidNetworking
import im.dacer.kata.R
import im.dacer.kata.core.data.MultiprocessPref
import im.dacer.kata.core.extension.urlEncode
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Dacer on 04/02/2018.
 * Thanks Mercury
 */
class WebParser {

    enum class Parser(val stringRes: Int) {
        DO_NOT_USE(R.string.web_page_parser_not_use),
        MERCURY(R.string.web_page_parser_mercury),
        URL2IO(R.string.web_page_parser_url2io)
    }

    companion object {

        fun getParserNameArray(context: Context) = WebParser.Parser.values().map { context.getString(it.stringRes) }.toTypedArray()

        fun getParseBy(name: String): Parser = Parser.valueOf(name)

        val DEFAULT_PARSER = Parser.MERCURY

        fun fetchContent(targetUrl: String, pref: MultiprocessPref): Observable<String>{
            return when (pref.webParser) {
                Parser.MERCURY -> fetchContentByMercury(targetUrl)
                Parser.URL2IO -> fetchContentByURL2IO(targetUrl)
                Parser.DO_NOT_USE -> Observable.just(targetUrl)
            }
        }

        private fun fetchContentByMercury(targetUrl: String): Observable<String> {
            return Rx2AndroidNetworking.get(getMercuryUrl(targetUrl))
                    .addHeaders("x-api-key", MERCURY_TOKEN)
                    .build()
                    .jsonObjectObservable
                    .map { return@map pareseHtml(it.getString("content")) }
                    .subscribeOn(Schedulers.io())
        }

        private fun fetchContentByURL2IO(targetUrl: String): Observable<String> {
            return Rx2AndroidNetworking.get(getURL2IOUrl(targetUrl))
                    .build()
                    .jsonObjectObservable
                    .map { return@map pareseHtml(it.getString("content")) }
                    .subscribeOn(Schedulers.io())
        }

        private fun pareseHtml(htmlContent: String): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT).toString().replace("\uFFFC", "")
            } else {
                Html.fromHtml(htmlContent).toString().replace("\uFFFC", "")
            }
        }

        private fun getMercuryUrl(targetUrl: String) =
                "https://mercury.postlight.com/parser?url=${targetUrl.urlEncode()}"

        private fun getURL2IOUrl(targetUrl: String) =
                "http://api.url2io.com/article?url=${targetUrl.urlEncode()}&token=$URL2IO_TOKEN"

        private const val MERCURY_TOKEN = "jq86fBWiadhIOAwqZH1tBHdDnVTCARLd6mGUNzx7"
        private const val URL2IO_TOKEN = "2Beu5yhqSriLt2vRsKkrCw"
    }
}