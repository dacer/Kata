package im.dacer.kata.ui.bigbang

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import im.dacer.kata.R
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.bigbang.History
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.data.room.dao.ContextStrDao
import im.dacer.kata.data.room.dao.HistoryDao
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.ContextFinder
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.engine.SearchEngine
import im.dacer.kata.util.helper.TTSHelper
import im.dacer.kata.util.segment.BigBang
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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
    @Inject lateinit var historyDao: HistoryDao
    @Inject lateinit var wordDao: WordDao
    @Inject lateinit var contextStrDao: ContextStrDao
    private var searchAction = SearchEngine.getDefaultSearchAction(context)

    private var currentSelectedToken: KanjiResult? = null
    private var kanjiResultList: List<KanjiResult>? = null
    private var segmentDis: Disposable? = null
    private var dictDisposable: Disposable? = null
    private var originText: String = ""

    override fun attachView(mvpView: BigbangMvp) {
        super.attachView(mvpView)
        ttsHelper.progressListener = object : TTSHelper.TTSPlayingListener {
            override fun isPlaying(playing: Boolean) {
                mvpView.showAudioBtnPlaying(playing)
            }
        }
    }

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
        val preselectedIndex = intent.data.getQueryParameter(BigBangActivity.EXTRA_PRESELECTED_INDEX)?.toInt()
        val saveInHistory = intent.data.getBooleanQueryParameter(BigBangActivity.EXTRA_SAVE_IN_HISTORY, true)
        val voiceUrl = intent.data.getQueryParameter(BigBangActivity.EXTRA_VOICE_URL)


        if (text.isEmpty()) {
            mvpView?.finish()
            return
        }
        if (voiceUrl != null && voiceUrl.isNotEmpty()) { mvpView?.showVoiceBtn(voiceUrl) }

        originText = text
        mvpView?.resetBigBangScrollViewPos()
        mvpView?.resetMeaningViewPos()
        segmentDis?.dispose()
        segmentDis = BigBang.getSegmentParserAsync()
                .flatMap { it.parse(text) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    mvpView?.onDataInitFinished(it, preselectedIndex)
                    kanjiResultList = it
                }, { mvpView?.toastError(it) })

        if(saveInHistory) historyDao.insert(History(text = text, alias = alias))
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

        onWordSelected(index)
        dictDisposable?.dispose()
        dictDisposable = searchDictHelper.searchForCombineResult(strForSearch, langUtils)
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
        mvpView?.showSystemUI()
    }

    fun onClickSearch() : Boolean {
        currentSelectedToken?.run { searchAction!!.start(context, this.strForSearch()) }
        return true
    }

    fun onClickAudio() : Boolean {
        try {
            ttsHelper.play(mvpView?.activity!!, currentSelectedToken?.baseForm)
        } catch (e: Exception) {
            mvpView?.toastError(e)
        }
        return true
    }

    fun onLongClickAudio() : Boolean {
        try {
            ttsHelper.play(mvpView?.activity!!, originText)
        } catch (e: Exception) {
            mvpView?.toastError(e)
        }
        return true
    }

    private fun onWordSelected(index: Int) {
        val kanjiResult = kanjiResultList?.get(index)!!
        wordDao.findByBaseForm(kanjiResult.baseForm)
                .subscribe {
                    val wordId = if (it.isEmpty()) {
                        wordDao.insert(Word(baseForm = kanjiResult.baseForm))
                    } else {
                        val word = it[0]
                        wordDao.update(word.afterSearchAgain())
                        word.id
                    }

                    contextStrDao.insert(ContextFinder.get(wordId, kanjiResultList!!, index))
                }
    }

}