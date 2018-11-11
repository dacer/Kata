package im.dacer.kata.util.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import collections.forEach
import com.afollestad.materialdialogs.MaterialDialog
import com.ichi2.anki.api.AddContentApi
import com.ichi2.anki.api.AddContentApi.READ_WRITE_PERMISSION
import im.dacer.kata.BuildConfig
import im.dacer.kata.R
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.util.extension.isInstalled
import im.dacer.kata.util.extension.openGooglePlay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


class AnkiDroidHelper @Inject constructor(@ApplicationContext val appContext: Context,
                                          val settingUtility: SettingUtility,
                                          val wordDao: WordDao) {

    private val api = AddContentApi(appContext)

    /**
     * filteredNotes -> the notes do not in AnkiDroid
     */
    private data class AnkiCardHelper(val allWords: List<Word>, val filteredNotes: List<Array<String>>)

    /**
     * check checkPermission before call this!
     */
    fun export(activity: Activity, moveToMasteredAfterExport: Boolean) {
        if (AddContentApi.getAnkiDroidPackageName(appContext) != null) {
            val deckId = getDeckId()
            val modelId = getModelId()
            val processDialog = MaterialDialog.Builder(activity).progress(true, 0).show()
            wordDao.loadNotMasteredMaybe()
                    .map { wordsToAnkiCardHelper(modelId, it) }
                    .map { helper ->
                        api.addNotes(modelId, deckId, helper.filteredNotes, null)
                        if (moveToMasteredAfterExport) {
                            helper.allWords.forEach { it.mastered = true }
                            wordDao.updateWords(*helper.allWords.toTypedArray())
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ processDialog.dismiss() },
                            {
                                processDialog.dismiss()
                                Timber.e(it)
                            })
        } else {
            showInstallAnkiDroidDialog(activity)
        }
    }

    private fun wordsToAnkiCardHelper(modelId: Long, words: List<Word>): AnkiCardHelper {
        val result = ArrayList(words)
        api.findDuplicateNotes(modelId, words.map { it.baseForm }).forEach { _, noteInfoList ->
            result.removeAll { it.baseForm == noteInfoList[0].key }
        }
        return AnkiCardHelper(words, result.map { arrayOf(it.baseForm, "") })
    }

    /**
     * return true if has permission
     */
    fun checkPermission(callbackActivity: Activity, callbackCode: Int): Boolean {
        if (!checkAnkiDroidAvailable(callbackActivity)) return false
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

    /**
     * return true if AnkiDroid installed
     */
    private fun checkAnkiDroidAvailable(activity: Activity): Boolean {
        if (!appContext.isInstalled(ANKIDROID_PACKAGE_NAME)) {
            showInstallAnkiDroidDialog(activity)
            return false
        }
        return true
    }

    private fun showInstallAnkiDroidDialog(activity: Activity) {
        MaterialDialog.Builder(activity)
                .title(R.string.ankidroid_not_found_title)
                .content(R.string.ankidroid_not_found_summary)
                .positiveText(R.string.ankidroid_not_found_install_btn)
                .negativeText(android.R.string.cancel)
                .onPositive { _, _ -> activity.openGooglePlay(ANKIDROID_PACKAGE_NAME) }
                .show()
    }

    companion object {
        private const val DECK_NAME = "Kata"
        private const val ANKIDROID_PACKAGE_NAME = "com.ichi2.anki"
        const val ANKI_PERMISSION_REQUEST = 123
    }
}