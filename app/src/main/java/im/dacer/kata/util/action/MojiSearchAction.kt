package im.dacer.kata.util.action

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import im.dacer.kata.R
import org.jetbrains.anko.toast


class MojiSearchAction : Action {

    override fun start(context: Context, text: String) {
        if (!TextUtils.isEmpty(text)) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            sendIntent.setClassName("com.mojitec.mojidict", "com.mojitec.mojidict.ui.SplashActivity")
            if (sendIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(sendIntent)
            } else {
                context.toast(R.string.moji_not_found)
            }
        }
    }

    companion object {

        fun create(): MojiSearchAction {
            return MojiSearchAction()
        }
    }
}
