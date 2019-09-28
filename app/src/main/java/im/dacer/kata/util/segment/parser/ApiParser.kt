package im.dacer.kata.util.segment.parser

import android.support.v4.util.PatternsCompat
import im.dacer.kata.data.model.bigbang.KuromojiApiResult
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.util.extension.getSubtitle
import im.dacer.kata.util.extension.isNewLine
import im.dacer.kata.util.helper.KanaHelper
import im.dacer.kata.util.helper.KuromojiApiHelper
import im.dacer.kata.util.segment.Parser
import io.reactivex.Observable

/**
 * Created by Dacer on 21/09/2019.
 */

class ApiParser : Parser<List<KanjiResult>> {
    override fun parse(text: String): Observable<List<KanjiResult>> {
        indexInUrlResult = 0
        val urlMatch = PatternsCompat.WEB_URL.matcher(text)
        urlInResultList.clear()

        while (urlMatch.find()) {
            urlInResultList.add(UrlInResult(urlMatch.start(), text.substring(urlMatch.start(), urlMatch.end())))
        }
        val textWithoutUrl = urlMatch.replaceAll("")
        return KuromojiApiHelper.search(textWithoutUrl)
                .map { it.toKanjiResultList() }
                .map {
                    val result = arrayListOf<KanjiResult>()
                    var index = 0
                    it.forEach {
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

                    return@map result
                }
    }
}

    private var indexInUrlResult = 0
    private val urlInResultList: ArrayList<UrlInResult> = arrayListOf()

    private fun KuromojiApiResult.toKanjiResultList(): List<KanjiResult> {
        val result: ArrayList<KanjiResult> = arrayListOf()
        for (token in tokens) {
            var surface = token.surface

            //split multi spaces to different KanjiResult
            if (surface.matches(Regex("^ +$"))) {
                repeat(surface.length) { result.add(KanjiResult(" ")) }
                continue
            }

            //remove \n in the end of surface and add KanjiResult.NEW_LINE instead
            var newLineNumber = 0
            while (surface.endsWith("\n") && !surface.isNewLine()) {
                surface = surface.substring(0, surface.length - 1)
                newLineNumber++
            }
            result.add(KanjiResult(surface, token.baseForm,
                    KanaHelper.toHiragana(token.reading), token.isKnown, token.getSubtitle()))
            result.add(KanjiResult.NEW_LINE(newLineNumber))
        }
        result.add(KanjiResult.NEW_LINE)
        return result
    }

    private fun getNextUrlInResult(): UrlInResult? {
        if (!urlInResultList.isEmpty() && urlInResultList.size > indexInUrlResult) {
            return urlInResultList[indexInUrlResult]
        }
        return null
    }

    private class UrlInResult(val startIndex: Int, val urlLink: String)
