package im.dacer.kata.segment.model

import im.dacer.kata.segment.util.KanaHelper
import im.dacer.kata.segment.util.isKana

/**
 * Created by Dacer on 10/01/2018.
 */
data class KanjiResult(val surface: String, val baseForm: String = "", val furigana: String = "") {

    /**
     * remove the exist kana in the surface for furigana
     * e.g
     * 　　　　うちあげ　　 うちあ
     *      　打ち上げ　→　打ち上げ
     */
    val furiganaForDisplay: String
    val furiganaStartOffset: Int
    val furiganaEndOffset: Int
    val needShowFurigana: Boolean

    init {
        needShowFurigana = KanaHelper.hasKanji(surface) && KanaHelper.hasHiragana(furigana)

        var result = furigana
        var startCount = 0
        var endCount = 0

        if (needShowFurigana) {
            surface.toCharArray()
                    .takeWhile { it.isKana() }
                    .forEach { startCount+=1 }

            if (result.isNotEmpty()) {
                result = result.replaceRange(0, startCount, "")
            }
            if (result.isNotEmpty()) {
                surface.toCharArray().reversed()
                        .takeWhile { it.isKana() }
                        .forEach { endCount+=1 }

                if (endCount < result.length) {
                    result = result.replaceRange(result.length - endCount, result.length, "")
                }
            }
        }

        furiganaStartOffset = startCount
        furiganaEndOffset = endCount
        furiganaForDisplay = result
    }
}