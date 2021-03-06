package im.dacer.kata.ui.main.wordbook

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.model.bigbang.WordWithMeaning
import im.dacer.kata.data.model.segment.CombinedResult
import im.dacer.kata.data.room.dao.ContextStrDao
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.SnackBarHelper
import im.dacer.kata.util.helper.AnkiDroidHelper
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject


@ConfigPersistent
class WordBookPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<WordBookMvp>() {

    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var appPref: MultiprocessPref
    @Inject lateinit var wordDao: WordDao
    @Inject lateinit var contextStrDao: ContextStrDao
    @Inject lateinit var ankiDroidHelper: AnkiDroidHelper
    @Inject lateinit var searchDictHelper: SearchDictHelper
    @Inject lateinit var langUtils: LangUtils

    private var refreshWordDis: Disposable? = null
    private var showLearning = true

    val swipeListener = object : OnItemSwipeListener {

        override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
            val word = mvpView?.getWordWithMeaning(pos)?.word ?: return
            SnackBarHelper.showRedo(mvpView!!.getDecorView(), getStr(R.string.deleted_sth, word.baseForm), { _ ->
                contextStrDao.findByWordId(word.id).subscribe { contextStrList ->
                    contextStrList.forEach{ contextStrDao.delete(it) }
                }
                wordDao.delete(word)
            }) {
                refreshWordList()
            }
        }

        override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}
        override fun onItemSwipeMoving(canvas: Canvas?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, isCurrentlyActive: Boolean) {}

    }

    override fun attachView(mvpView: WordBookMvp) {
        super.attachView(mvpView)

    }

    fun onResume() {
        refreshWordList()
    }

    override fun detachView() {
        super.detachView()
        refreshWordDis?.dispose()
    }

    fun onExportAnkiClicked(activity: Activity?) {
        if (activity == null) return
        if (ankiDroidHelper.checkPermission(activity, AnkiDroidHelper.ANKI_PERMISSION_REQUEST)) {
            exportAnki(activity)
        }
    }

    fun onWordClicked(pos: Int) {
        val word = mvpView?.getWordWithMeaning(pos)?.word ?: return
        if (!showLearning) {
            wordDao.update(word.markLearning())
            SnackBarHelper.showRedo(mvpView!!.getDecorView(),
                    getStr(R.string.mark_sth_as_learning, word.baseForm), {}) {
                wordDao.update(word.markMastered())
            }
        }
    }

    fun onClickChangeList(): Boolean {
        showLearning = !showLearning
        mvpView?.showFlashcardBtn(showLearning)
        refreshWordList()
        return true
    }

    /**
     * check checkPermission before call this!
     */
    fun exportAnki(activity: Activity) {
        MaterialDialog.Builder(activity)
                .title(R.string.ankidroid_export_title)
                .content(R.string.ankidroid_export_summary)
                .checkBoxPromptRes(R.string.ankidroid_export_move_to_mastered_after_export, settingUtility.moveToMasteredAfterExport, null)
                .positiveText(R.string.ankidroid_export_export_btn)
                .negativeText(android.R.string.cancel)
                .onPositive { dialog, _ ->
                    settingUtility.moveToMasteredAfterExport = dialog.isPromptCheckBoxChecked
                    ankiDroidHelper.export(activity, dialog.isPromptCheckBoxChecked)
                }
                .show()
    }

    private fun refreshWordList() {
        mvpView?.setLoading(true)
        if (getWordSize() == 0) {
            mvpView?.setLoading(false)
            return
        }
        mvpView?.setChangeListMenuName(if (showLearning) getStr(R.string.learning) else getStr(R.string.mastered))
        refreshWordDis?.dispose()
        refreshWordDis = getLoadWordFlowable()
                .toObservable()
                .flatMapIterable { it }
                .flatMap {
                    Observable.zip(
                            Observable.just(it),
                            searchDictHelper.searchForCombineResultAndTranslateIfNoMeaning(it.baseForm, langUtils),
                            BiFunction<Word, CombinedResult, WordWithMeaning> {
                                word, combineResult ->
                                WordWithMeaning(word, combineResult.getMeaning(context))
                            }
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { word ->
                    mvpView?.addWordWithMeaning(word)
                    mvpView?.setLoading(false)
                }
    }

    private fun getLoadWordFlowable(): Flowable<List<Word>> {
        return if (showLearning) {
            wordDao.loadNotMasteredFlowable()
        } else {
            wordDao.loadMastered()
        }
    }

    private fun getWordSize(): Int {
        return if (showLearning) {
            wordDao.getNotMasteredNum()
        } else {
            wordDao.getMasteredNum()
        }
    }

    private fun getStr(resId: Int, vararg formatArgs: String = arrayOf()): String {
        return context.getString(resId, *formatArgs)
    }
}