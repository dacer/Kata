package im.dacer.kata.util.segment.parser

import androidx.core.util.PatternsCompat
import com.atilika.kuromoji.TokenizerBase
import com.atilika.kuromoji.ipadic.Tokenizer
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.util.extension.toKanjiResultList
import im.dacer.kata.util.segment.SimpleParser

/**
 * Created by Dacer on 09/01/2018.
 */

class KuromojiParser : SimpleParser() {

    private val tokenizer = Tokenizer.Builder().mode(TokenizerBase.Mode.NORMAL).build()
    private var indexInUrlResult = 0
    private val urlInResultList: ArrayList<UrlInResult> = arrayListOf()

    override fun parseSync(text: String): List<KanjiResult> {
        indexInUrlResult = 0
        val urlMatch = PatternsCompat.WEB_URL.matcher(text)
        urlInResultList.clear()

        while (urlMatch.find()) {
            urlInResultList.add(UrlInResult(urlMatch.start(), text.substring(urlMatch.start(), urlMatch.end())))
        }
        val textWithoutUrl = urlMatch.replaceAll("")
        val kanjiResultList = tokenizer.tokenize(textWithoutUrl).toKanjiResultList()
        val result = arrayListOf<KanjiResult>()

        var index = 0
        kanjiResultList.forEach {
            getNextUrlInResult()?.run {
                if (index == this.startIndex) {
                    result.add(KanjiResult(this.urlLink, isUrl = true))
                    index += this.urlLink.length
                    indexInUrlResult++
                }
            }
            result.add(it)
            index += it.surface.length
        }

        return result
    }

    private fun getNextUrlInResult(): UrlInResult? {
        if (!urlInResultList.isEmpty() && urlInResultList.size > indexInUrlResult) {
            return urlInResultList[indexInUrlResult]
        }
        return null
    }

    private class UrlInResult(val startIndex: Int, val urlLink: String)
}
