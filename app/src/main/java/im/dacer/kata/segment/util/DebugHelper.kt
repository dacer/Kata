package im.dacer.kata.segment.util

/**
 * Created by Dacer on 21/01/2018.
 */
object DebugHelper {
    fun removeUnicodeAndEscapeChars(input: String): String {
        val buffer = StringBuilder(input.length)
        for (i in 0 until input.length) {
            if (input[i].toInt() > 256) {
                buffer.append("\\u").append(Integer.toHexString(input[i].toInt()))
            } else {
                if (input[i] == '\n') {
                    buffer.append("\\n")
                } else if (input[i] == '\t') {
                    buffer.append("\\t")
                } else if (input[i] == '\r') {
                    buffer.append("\\r")
                } else if (input[i] == '\b') {
                    buffer.append("\\b")
                } else if (input[i] == '\'') {
                    buffer.append("\\'")
                } else if (input[i] == '\"') {
                    buffer.append("\\")
                } else if (input[i] == '\\') {
                    buffer.append("\\\\")
                } else {
                    buffer.append(input[i])
                }
            }
        }
        return buffer.toString()
    }
}