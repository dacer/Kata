package im.dacer.kata.util.extension

import com.atilika.kuromoji.ipadic.Token
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.util.helper.KanaHelper

/**
 * Created by Dacer on 13/01/2018.
 */

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

//fun Token.toKanjiResult(): KanjiResult {
//    return KanjiResult(surface, baseForm,
//            KanaHelper.toHiragana(reading), isKnown, getSubtitle())
//}

fun List<Token>.toKanjiResultList(): List<KanjiResult> {
    val result: ArrayList<KanjiResult> = arrayListOf()
    for (token in this) {
        var surface = token.surface
        var newLineNumber = 0
        while (surface.endsWith("\n") && !surface.isNewLine()) {
            surface = surface.substring(0, surface.length - 1)
            newLineNumber++
        }
        result.add(KanjiResult(surface, token.baseForm,
                KanaHelper.toHiragana(token.reading), token.isKnown, token.getSubtitle()))
        while (newLineNumber > 0) {
            result.add(KanjiResult.NEW_LINE)
            newLineNumber--
        }

    }

    return result
}

private fun String.isNewLine(): Boolean {
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
