package im.dacer.kata.util.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.util.PatternsCompat.WEB_URL
import im.dacer.kata.R
import im.dacer.kata.util.helper.SchemeHelper
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

fun String.isUrl(): Boolean {
    return WEB_URL.matcher(this).matches()
}

fun String.copyToClipboard(context: Context, showInKata: Boolean = true) {
    val service = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    service.setPrimaryClip(ClipData.newPlainText(if(showInKata) "" else SchemeHelper.IGNORE_CLIP_DATA_LABEL, this))
    Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
}
