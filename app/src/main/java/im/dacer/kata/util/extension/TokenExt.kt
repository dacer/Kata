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

fun Token.toKanjiResult(): KanjiResult {
    return KanjiResult(surface, baseForm, KanaHelper.toHiragana(reading))
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
