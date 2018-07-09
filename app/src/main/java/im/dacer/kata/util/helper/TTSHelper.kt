package im.dacer.kata.util.helper

import android.app.Activity
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.kata.R
import im.dacer.kata.injection.qualifier.ApplicationContext
import java.util.*
import javax.inject.Inject


/**
 * Created by Dacer on 01/02/2018.
 */
class TTSHelper @Inject constructor(@ApplicationContext appContext: Context): UtteranceProgressListener() {

    private var available = false
    private val initListener = TextToSpeech.OnInitListener {
        if (it == TextToSpeech.SUCCESS) {
            val result = this.tts.setLanguage(Locale.JAPAN)
            if (result == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                available = true
            }
            tts.setOnUtteranceProgressListener(this)
        }
    }
    private val tts: TextToSpeech = TextToSpeech(appContext, initListener)
    private var ttsStarted = false

    override fun onDone(utteranceId: String?) {
        ttsStarted = false
    }

    override fun onError(utteranceId: String?) {}

    override fun onStart(utteranceId: String?) {
        ttsStarted = true
    }

    fun play(activity: Activity, string: String?) {
        if (ttsStarted) {
            onDone("")
            tts.stop()
            return
        }
        if (string.isNullOrEmpty()) return

        if (available) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(string, TextToSpeech.QUEUE_FLUSH, null, "aaa")
            } else {
                tts.speak(string, TextToSpeech.QUEUE_FLUSH, null)
            }
        } else {
            MaterialDialog.Builder(activity)
                    .title(R.string.tts_error_title)
                    .content(R.string.tts_error_content)
                    .positiveText(android.R.string.ok)
                    .onPositive { _, _ -> }
                    .show()
        }
    }

    fun onDestroy() {
        tts.stop()
        tts.shutdown()
    }


}