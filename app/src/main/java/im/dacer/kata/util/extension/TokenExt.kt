package im.dacer.kata.util.extension

import com.atilika.kuromoji.ipadic.Token
import im.dacer.kata.data.model.bigbang.KuromojiApiResult
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.util.helper.KanaHelper

/**
 * Created by Dacer on 13/01/2018.
 */

fun KuromojiApiResult.Token.getSubtitle(): String {
    val list = arrayListOf<String>()
    conjugationType.addToList(list)
    partOfSpeechLevel2.addToList(list)
    partOfSpeechLevel3.addToList(list)
    partOfSpeechLevel4.addToList(list)

    if (list.isEmpty()) {
        return partOfSpeechLevel1
    }
    return "$partOfSpeechLevel1 (${list.joinToString(", ")})"
}

fun Token.getSubtitle(): String {
    val list = arrayListOf<String>()
    conjugationType.addToList(list)
    partOfSpeechLevel2.addToList(list)
    partOfSpeechLevel3.addToList(list)
    partOfSpeechLevel4.addToList(list)

    if (list.isEmpty()) {
        return partOfSpeechLevel1
    }
    return "$partOfSpeechLevel1 (${list.joinToString(", ")})"
}

/**
 * Will add a new line in the end
 */
fun List<Token>.toKanjiResultList(): List<KanjiResult> {
    val result: ArrayList<KanjiResult> = arrayListOf()
    for (token in this) {
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

fun String.isNewLine(): Boolean {
    return this.replace("\n", "").isEmpty()
}

fun Token.strForSearch(): String {
    return if (isKnown) baseForm else surface
}

private fun String.addToList(list: ArrayList<String>) {
    if (!isAsteriskOrEmpty()) list.add(this)
}

private fun String.isAsteriskOrEmpty(): Boolean {
    return this.isBlank() || this.replace(" ", "") == "*"
}
