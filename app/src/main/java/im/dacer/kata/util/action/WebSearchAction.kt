package im.dacer.kata.util.action

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

abstract class WebSearchAction : Action {

    override fun start(context: Context, text: String) {
        if (!TextUtils.isEmpty(text)) {
            val intent = Intent(Intent.ACTION_VIEW, createSearchUri(text))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private fun createSearchUri(text: String): Uri {
        var encode: String? = null
        try {
            encode = URLEncoder.encode(text, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return createSearchUriWithEncodedText(encode)
    }

    abstract fun createSearchUriWithEncodedText(encodedText: String?): Uri

}
