package im.dacer.kata.core.extension

import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Created by Dacer on 04/02/2018.
 */
fun String.urlEncode() = URLEncoder.encode(this, "UTF-8")
fun String.urlDecode() = URLDecoder.decode(this, "UTF-8")

/**
 * return null if url not exist
 */
fun String.findUrl(): String? {
    val regex = Regex("(http|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?")
    regex.find(this)?.run {
        return this.value
    }
    return null
}
