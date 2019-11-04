package im.dacer.kata.util.action

import android.content.Context
import android.content.Intent
import android.text.TextUtils

class ShareAction : Action {

    override fun start(context: Context, text: String) {
        if (!TextUtils.isEmpty(text)) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
            context.startActivity(sharingIntent)
        }
    }

    companion object {

        fun create(): ShareAction {
            return ShareAction()
        }
    }
}
