package im.dacer.kata.util

import im.dacer.kata.data.model.bigbang.ContextStr
import im.dacer.kata.data.model.segment.KanjiResult

/**
 * return ContextStr for a word in KanjiResults
 */
class ContextFinder {
    companion object {
        private val BREAK_SYMBOL = arrayOf('\u000a', '。')
        private const val MAX_CONTEXT_LENGTH = 50
        private const val MIN_STR_AFTER_LENGTH = 10

        fun get(wordId: Long, kanjiResultList: List<KanjiResult>, index: Int): ContextStr {
            val selectedResult = kanjiResultList[index]
            var strBefore = ""
            for (i in index-1 downTo 0) {
                val result = kanjiResultList[i]
                val lastChar = if (result.surface.isEmpty()) '\u0000' else result.surface.last()
                if (BREAK_SYMBOL.contains(lastChar)) {
                    break
                }
                strBefore = "${result.surface}$strBefore"
            }

            var strAfter = ""
            for (i in index+1 until kanjiResultList.size) {
                val result = kanjiResultList[i]
                if (result.surface == "。") {
                    strAfter += "。"
                    break
                }
                if (strBefore.length + selectedResult.surface.length + strAfter.length > MAX_CONTEXT_LENGTH &&
                        strAfter.length >= MIN_STR_AFTER_LENGTH) {
                    strAfter += "..."
                    break
                }
                strAfter += result.surface
            }
            return ContextStr(
                    wordId = wordId,
                    text = "$strBefore${selectedResult.surface}$strAfter",
                    fromIndex = strBefore.length,
                    toIndex = strBefore.length + selectedResult.surface.length)
        }

        private fun toUnicode(ch: Char): String {
            return String.format("\\u%04x", ch.toInt())
        }
    }

}