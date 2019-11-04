package im.dacer.kata.util.action

import android.content.Context
import android.content.Intent
import android.text.TextUtils

class MojiSearchAction : Action {

    override fun start(context: Context, text: String) {
        if (!TextUtils.isEmpty(text)) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            sendIntent.setClassName("com.mojitec.mojidict", "com.mojitec.mojidict.ui.SplashActivity")
            context.startActivity(sendIntent)
        }
    }

    companion object {

        fun create(): MojiSearchAction {
            return MojiSearchAction()
        }
    }
}
