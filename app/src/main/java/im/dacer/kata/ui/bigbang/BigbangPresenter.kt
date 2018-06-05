package im.dacer.kata.ui.bigbang

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import im.dacer.kata.R
import im.dacer.kata.data.local.HistoryHelper
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.bigbang.DictEntry
import im.dacer.kata.data.model.bigbang.DictReading
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.engine.SearchEngine
import im.dacer.kata.util.extension.toKanjiResultList
import im.dacer.kata.util.helper.TTSHelper
import im.dacer.kata.util.segment.BigBang
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Dacer on 13/02/2018.
 */

@ConfigPersistent
class BigbangPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<BigbangMvp>() {
    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var langUtils: LangUtils
    @Inject lateinit var searchDictHelper: SearchDictHelper
    @Inject lateinit var ttsHelper: TTSHelper
    private var searchAction = SearchEngine.getDefaultSearchAction(context)

    private var currentSelectedToken: KanjiResult? = null
    private var kanjiResultList: List<KanjiResult>? = null
    private var segmentDis: Disposable? = null
    private var dictDisposable: Disposable? = null


    override fun detachView() {
        super.detachView()
        dictDisposable?.dispose()
        segmentDis?.dispose()
        searchDictHelper.onDestroy()
        ttsHelper.onDestroy()
    }

    fun changeAndFireSearchAction(menuItem: MenuItem): Boolean {
        searchAction = SearchEngine.getSearchAction(menuItem.title.toString())
        onClickSearch()
        return true
    }

    fun handIntent(intent: Intent) {
        val text = intent.data.getQueryParameter(BigBangActivity.EXTRA_TEXT)
        val alias = intent.data.getQueryParameter(BigBangActivity.EXTRA_ALIAS)
        val preselectedIndex = intent.data.getQueryParameter(BigBangActivity.PRESELECTED_INDEX)?.toInt()
        val saveInHistory = intent.data.getBooleanQueryParameter(BigBangActivity.SAVE_IN_HISTORY, true)

        if (text.isEmpty()) {
            mvpView?.finish()
            return
        }
        segmentDis?.dispose()
        segmentDis = BigBang.getSegmentParserAsync()
                .flatMap { it.parse(text) }
                .flatMap { Observable.fromIterable(it.toKanjiResultList()) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    mvpView?.onDataInitFinished(it, preselectedIndex)
                    kanjiResultList = it
                }, { mvpView?.toastError(it) })

        if(saveInHistory) HistoryHelper.saveAsync(context, text, alias)
    }

    fun onItemClicked(index: Int) {
        currentSelectedToken = kanjiResultList?.get(index)
        val strForSearch: String

        mvpView?.pronunciationText = null
        if (currentSelectedToken?.isKnown == true) {
            mvpView?.descText = "[${currentSelectedToken?.baseForm}] ${currentSelectedToken?.subtitle}"
            mvpView?.meaningText = ""
            strForSearch = currentSelectedToken!!.baseForm

        } else {
            mvpView?.descText = currentSelectedToken?.surface
            strForSearch = currentSelectedToken?.surface ?: ""
        }

        dictDisposable?.dispose()
        dictDisposable = Observable.fromCallable{ searchDictHelper!!.search(strForSearch) }
                .flatMap {
                    Observable.zip(
                            dealWithDictEntryList(it.dictEntryList),
                            dealWithDictReadingList(it.dictReadingList),
                            BiFunction<String, String, CombinedResult> {
                                meaningStr, readingStr -> CombinedResult(meaningStr, readingStr)
                            }
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val meaningStr = it.meaningStr
                    mvpView?.meaningText = if (meaningStr.isBlank()) {
                        context.getString(R.string.not_found_error, strForSearch)
                    } else { meaningStr }

                    val readingStr = it.readingStr
                    if (readingStr.isNotEmpty() && readingStr.contains(",")) {
                        mvpView?.pronunciationText = readingStr
                    }
                }, { mvpView?.toastError(it) })

    }

    fun onClickSearch() : Boolean {
        currentSelectedToken?.run { searchAction!!.start(context, this.strForSearch()) }
        return true
    }

    fun onClickAudio() : Boolean {
        try {
            currentSelectedToken?.run { ttsHelper.play(mvpView?.activity!!, this.baseForm) }
        } catch (e: Exception) {
            mvpView?.toastError(e)
        }
        return true
    }

    private fun dealWithDictEntryList(dictEntryList: List<DictEntry>): Observable<String> {
        return Observable.fromIterable(dictEntryList)
                .flatMap { langUtils.fetchTranslation(it) }
                .toList()
                .map { it.joinToString("\n\n") { "· $it" } }
                .toObservable()
    }

    private fun dealWithDictReadingList(dictReadingList: List<DictReading>?): Observable<String> {
        return Observable.fromCallable { dictReadingList?.joinToString(",")
        {it.reading() ?: ""} ?: "" }
    }

    private data class CombinedResult(val meaningStr: String, val readingStr: String)

}