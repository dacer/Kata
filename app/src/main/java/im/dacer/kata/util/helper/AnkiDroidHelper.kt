package im.dacer.kata.util.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.ichi2.anki.api.AddContentApi
import com.ichi2.anki.api.AddContentApi.READ_WRITE_PERMISSION
import im.dacer.kata.BuildConfig
import im.dacer.kata.R
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.room.dao.ContextStrDao
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.extension.openGooglePlay
import im.dacer.kata.util.extension.toast
import im.dacer.kata.util.extension.urlEncode
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.collections.forEach
import timber.log.Timber
import javax.inject.Inject


class AnkiDroidHelper @Inject constructor(@ApplicationContext val appContext: Context,
                                          val settingUtility: SettingUtility,
                                          val wordDao: WordDao,
                                          val searchDictHelper: SearchDictHelper,
                                          val langUtils: LangUtils,
                                          val contextStrDao: ContextStrDao) {

    private val api = AddContentApi(appContext)

    /**
     * filteredNotes -> the notes do not in AnkiDroid
     */
    private data class AnkiCardHelper(val allWords: List<Word>, val filteredNotes: List<Array<String>>)

    /**
     * check checkPermission before call this!
     */
    @SuppressLint("CheckResult")
    fun export(activity: Activity, moveToMasteredAfterExport: Boolean) {
        val deckId = getDeckId()
        val modelId = getModelId()
        val processDialog = MaterialDialog.Builder(activity).progress(true, 0).show()
        wordDao.loadNotMasteredMaybe()
                .flatMap { wordsToAnkiCardHelper(modelId, it) }
                .map { helper ->
                    api.addNotes(modelId, deckId, helper.filteredNotes, null)
                    if (moveToMasteredAfterExport) {
                        helper.allWords.forEach { it.mastered = true }
                        wordDao.updateWords(*helper.allWords.toTypedArray())
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    processDialog.dismiss()
                    activity.toast(R.string.ankidroid_export_finished)
                },
                        {
                            processDialog.dismiss()
                            Timber.e(it)
                        })
    }

    private fun wordsToAnkiCardHelper(modelId: Long, words: List<Word>): Maybe<AnkiCardHelper> {
        val result = ArrayList(words)
        api.findDuplicateNotes(modelId, words.map { it.baseForm }).forEach { _, noteInfoList ->
            result.removeAll { it.baseForm == noteInfoList[0].key }
        }
        return Observable.fromIterable(result)
                .flatMap { searchDictHelper.searchForCombineResultFull(it, langUtils, contextStrDao) }
                .map {
                    arrayOf(it.strForSearch,
                            it.meaningStr,
                            it.readingStr,
                            it.contextStr.toHtmlString(),
                            "")
                }
                .toList()
                .map { AnkiCardHelper(words, it) }
                .toMaybe()
    }

    private fun CharSequence?.toHtmlString(): String {
        return this?.replace(Regex("\n"), "<br>") ?: ""
    }

    private fun getJapanesePodAudioUrl(kanji: String, kana: String): String {
        return "[sound:https://assets.languagepod101.com/dictionary/japanese/audiomp3.php?kanji=${kanji.urlEncode()}&kana=${kana.urlEncode()}]"
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
            modelId = api.addNewCustomModel(ANKI_MODEL_NAME, FIELDS, CARD_NAMES, FRONT_LAYOUT,
                    BACK_LAYOUT, CSS, getDeckId(), null)
            settingUtility.ankiModelId = modelId
        }
        return modelId
    }

    /**
     * return true if AnkiDroid installed
     */
    private fun checkAnkiDroidAvailable(activity: Activity): Boolean {
        if (AddContentApi.getAnkiDroidPackageName(appContext) == null) {
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

        const val ANKI_MODEL_NAME = "${BuildConfig.APPLICATION_ID}-v1"

        private val FIELDS = arrayOf("Expression", "Meaning", "Reading", "ContextStr", "Audio")
        private val CARD_NAMES = arrayOf("Card 1")
        private val FRONT_LAYOUT = arrayOf("""
            <div class=expression>{{Expression}}</div>
            <hr id=answer>
            <div class=context>{{ContextStr}}</div>
        """.trimIndent())
        private val BACK_LAYOUT = arrayOf("""
            <div class=expression>{{Expression}}</div>
            <hr id=answer>
            <div class=reading>{{Reading}}</div><br>
            {{Meaning}}
            <!--{{Audio}}-->
        """.trimIndent())
        private const val CSS = """
            .card {
             font-family: arial;
             font-size: 20px;
             text-align: center;
             color: black;
             background-color: white;
            }
            .expression {
             font-size: 56px;
            }
            .reading {
             font-size: 42px;
            }
            .context {
             font-size: 14px;
            }
        """
    }
}