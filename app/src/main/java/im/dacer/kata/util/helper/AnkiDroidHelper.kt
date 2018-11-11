package im.dacer.kata.util.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.ichi2.anki.api.AddContentApi
import com.ichi2.anki.api.AddContentApi.READ_WRITE_PERMISSION
import im.dacer.kata.BuildConfig
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.injection.qualifier.ApplicationContext
import javax.inject.Inject


class AnkiDroidHelper @Inject
constructor(@ApplicationContext val appContext: Context, val settingUtility: SettingUtility) {

    private val api = AddContentApi(appContext)

    fun export() {
        if (AddContentApi.getAnkiDroidPackageName(appContext) != null) {
            val deckId = getDeckId()
            val modelId = getModelId()
            api.addNotes(modelId, deckId, generateCards(), null)
        } else {
            //todo show install anki alert
        }
    }

    /**
     * return true if has permission
     */
    fun checkPermission(callbackActivity: Activity, callbackCode: Int): Boolean {
        if (shouldRequestPermission()) {
            requestPermission(callbackActivity, callbackCode)
            return false
        }
        return true
    }

    private fun shouldRequestPermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            false
        } else ContextCompat.checkSelfPermission(appContext, READ_WRITE_PERMISSION) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(callbackActivity: Activity, callbackCode: Int) {
        ActivityCompat.requestPermissions(callbackActivity, arrayOf(READ_WRITE_PERMISSION), callbackCode)
    }

    private fun generateCards(): List<Array<String>>{

    }

    private fun getDeckId(): Long {
        var deckId = -1L
        if (settingUtility.ankiDeckId != -1L) {
            val result = api.deckList.filter { it.key == settingUtility.ankiDeckId }
            if (result.isNotEmpty()) {
                deckId = settingUtility.ankiDeckId
            }
        }
        if (deckId == -1L) {
            deckId = api.addNewDeck(DECK_NAME)
            settingUtility.ankiDeckId = deckId
        }
        return deckId
    }

    private fun getModelId(): Long {
        var modelId = -1L
        if (settingUtility.ankiModelId != -1L) {
            val result = api.modelList.filter { it.key == settingUtility.ankiModelId }
            if (result.isNotEmpty()) {
                modelId = settingUtility.ankiModelId
            }
        }
        if (modelId == -1L) {
            modelId = api.addNewBasicModel(BuildConfig.APPLICATION_ID)
            settingUtility.ankiModelId = modelId
        }
        return modelId
    }

    companion object {
        private const val DECK_NAME = "Kata"
        const val ANKI_PERMISSION_REQUEST = 123
    }
}