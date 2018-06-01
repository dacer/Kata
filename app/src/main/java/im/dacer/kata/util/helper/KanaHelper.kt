package im.dacer.kata.util.helper

/**
 * KanaHelper contains static functions to do various tests
 * on characters to determine if it is one of the various types of
 * characters used in the japanese writing system.
 *
 *
 * There are also a functions to translate between Katakana, Hiragana,
 * and Romaji.
 *
 * @author Duane J. May <djmay></djmay>@mayhoo.com>
 * @version $Id: KanaHelper.java,v 1.2 2002/04/20 18:10:24 djmay Exp $
 * @since 10:37 AM - 6/3/14
 *
 * https://gist.github.com/mediavrog/6b13669533ac20d5ba0f
 *
 * @see [http://sourceforge.net/projects/kanjixml/](http://sourceforge.net/projects/kanjixml/)
 */

fun Char.isKana(): Boolean = KanaHelper.isKana(this)
fun String.hasKanjiOrKana(): Boolean = this.toCharArray().any { KanaHelper.isKanji(it) || KanaHelper.isKana(it) }

object KanaHelper {

    /**
     * Version information
     */
    private val VERSION = "\$Id: KanaHelper.java,v 1.2 2002/04/20 18:10:24 djmay Exp $"

    /**
     * The array used to map hirgana to romaji.
     */
    internal var romaji = arrayOf("a", "a", "i", "i", "u", "u", "e", "e", "o", "o",

            "ka", "ga", "ki", "gi", "ku", "gu", "ke", "ge", "ko", "go",

            "sa", "za", "shi", "ji", "su", "zu", "se", "ze", "so", "zo",

            "ta", "da", "chi", "ji", "tsu", "tsu", "zu", "te", "de", "to", "do",

            "na", "ni", "nu", "ne", "no",

            "ha", "ba", "pa", "hi", "bi", "pi", "fu", "bu", "pu", "he", "be", "pe", "ho", "bo", "po",

            "ma", "mi", "mu", "me", "mo",

            "a", "ya", "u", "yu", "o", "yo",

            "ra", "ri", "ru", "re", "ro",

            "wa", "wa", "wi", "we", "o", "n",

            "v", "ka", "ke")

    /**
     * Determines if this character is a Japanese Kana.
     */
    fun isKana(c: Char): Boolean {
        return isHiragana(c) || isKatakana(c)
    }

    /**
     * Determines if this character is one of the Japanese Hiragana.
     */
    fun isHiragana(c: Char): Boolean {
        return c in '\u3041'..'\u309e'
    }

    /**
     * Determines if this character is one of the Japanese Katakana.
     */
    fun isKatakana(c: Char): Boolean {
        return isHalfWidthKatakana(c) || isFullWidthKatakana(c)
    }

    /**
     * Determines if this character is a Half width Katakana.
     */
    fun isHalfWidthKatakana(c: Char): Boolean {
        return c in '\uff66'..'\uff9d'
    }

    /**
     * Determines if this character is a Full width Katakana.
     */
    fun isFullWidthKatakana(c: Char): Boolean {
        return c in '\u30a1'..'\u30fe'
    }

    /**
     * Determines if this character is a Kanji character.
     */
    fun isKanji(c: Char): Boolean {
        if (c in '\u4e00'..'\u9fa5') {
            return true
        }
        return c in '\u3005'..'\u3007'
    }

    /**
     * Determines if this character could be used as part of
     * a romaji character.
     */
    fun isRomaji(c: Char): Boolean {
        return if (c in '\u0041'..'\u0090')
            true
        else if (c in '\u0061'..'\u007a')
            true
        else if (c in '\u0021'..'\u003a')
            true
        else if (c in '\u0041'..'\u005a')
            true
        else
            false
    }

    /**
     * Translates this character into the equivalent Katakana character.
     * The function only operates on Hiragana and always returns the
     * Full width version of the Katakana. If the character is outside the
     * Hiragana then the origianal character is returned.
     */
    fun toKatakana(c: Char): Char {
        return if (isHiragana(c)) {
            (c.toInt() + 0x60).toChar()
        } else c
    }

    /**
     * Translates this character into the equivalent Hiragana character.
     * The function only operates on Katakana characters
     * If the character is outside the Full width or Half width
     * Katakana then the origianal character is returned.
     */
    fun toHiragana(c: Char): Char {
        if (isFullWidthKatakana(c)) {
            return (c.toInt() - 0x60).toChar()
        } else if (isHalfWidthKatakana(c)) {
            return (c.toInt() - 0xcf25).toChar()
        }
        return c
    }

    /**
     * Translates this character into the equivalent Romaji character.
     * The function only operates on Hiragana and Katakana characters
     * If the character is outside the given range then
     * the origianal character is returned.
     *
     *
     * The resulting string is lowercase if the input was Hiragana and
     * UPPERCASE if the input was Katakana.
     */
    fun toRomaji(c: Char): String {
        var ch = c
        if (isHiragana(ch)) {
            return lookupRomaji(ch)
        } else if (isKatakana(ch)) {
            ch = toHiragana(ch)
            val str = lookupRomaji(ch)
            return str.toUpperCase()
        }
        return ch.toString()
    }

    /**
     * Access the array to return the correct romaji string.
     */
    private fun lookupRomaji(c: Char): String {
        return romaji[c.toInt() - 0x3041]
    }

    fun toHiragana(str: String): String {
        return str.map { toHiragana(it) }.joinToString("")
    }

    fun convertKana(input: String?): String {
        if (input == null || input.isEmpty()) return ""

        val out = StringBuilder()
        val ch = input[0]

        when {
            isHiragana(ch) -> // convert to hiragana to katakana
                for (i in 0 until input.length) {
                    out.append(toKatakana(input[i]))
                }
            isKatakana(ch) -> // convert to katakana to hiragana
                for (i in 0 until input.length) {
                    out.append(toHiragana(input[i]))
                }
            else -> // do nothing if neither
                return input
        }
        return out.toString()
    }

    fun hasKanji(str: String) : Boolean {
        return str.toCharArray().any { isKanji(it) }
    }

    fun hasHiragana(str: String) : Boolean {
        return str.toCharArray().any { isHiragana(it) }
    }
}