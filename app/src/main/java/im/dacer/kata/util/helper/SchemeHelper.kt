package im.dacer.kata.util.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import im.dacer.kata.R
import im.dacer.kata.ui.bigbang.BigBangActivity
import java.net.URLEncoder

/**
 * Created by Dacer on 09/01/2018.
 */
class SchemeHelper {
    companion object {
        const val SHOW_FLOAT_MAX_TEXT_COUNT = 99

        fun startKata(c: Context, text: String, preselectedIndex: Int = -1,
                      saveInHistory: Boolean = true, alias: String = "",
                      activity: Activity? = null, voiceUrl: String? = "") {
            val intent = Intent(Intent.ACTION_VIEW, getUri(text, preselectedIndex, saveInHistory, alias, voiceUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            c.startActivity(intent)
            activity?.overridePendingTransition(R.anim.activity_in, R.anim.activity_out)
        }

        fun startKataFloatDialog(c: Context, text: String) {
            val intent = Intent(Intent.ACTION_VIEW, getFloatUri(text))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            c.startActivity(intent)
        }

        private fun getUri(text: String, preselectedIndex: Int = 0, saveInHistory: Boolean = true,
                           alias: String = "", voiceUrl: String? = "") =
                Uri.parse("kata://?" +
                        "${BigBangActivity.EXTRA_TEXT}=${URLEncoder.encode(text, "utf-8")}" +
                        "&${BigBangActivity.EXTRA_PRESELECTED_INDEX}=$preselectedIndex" +
                        "&${BigBangActivity.EXTRA_SAVE_IN_HISTORY}=$saveInHistory" +
                        "&${BigBangActivity.EXTRA_ALIAS}=$alias" +
                        "&${BigBangActivity.EXTRA_VOICE_URL}=$voiceUrl")

        private fun getFloatUri(text: String) =
                Uri.parse("kata-float://?${BigBangActivity.EXTRA_TEXT}=${URLEncoder.encode(text, "utf-8")}")
    }
}