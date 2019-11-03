package im.dacer.kata.ui.bigbang

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SearchDictHelper
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
import im.dacer.kata.util.helper.hasKanjiOrKana
import im.dacer.kata.util.segment.BigBang
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Dacer on 13/02/2018.
 */

@ConfigPersistent
class BigbangPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<BigbangMvp>() {
    @Inject lateinit var langUtils: LangUtils
    @Inject lateinit var searchDictHelper: SearchDictHelper
    @Inject lateinit var ttsHelper: TTSHelper
    @Inject lateinit var historyDao: HistoryDao
    @Inject lateinit var wordDao: WordDao
    @Inject lateinit var contextStrDao: ContextStrDao
    @Inject lateinit var appPre: MultiprocessPref

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
        val text = intent.data?.getQueryParameter(BigBangActivity.EXTRA_TEXT)
        val alias = intent.data?.getQueryParameter(BigBangActivity.EXTRA_ALIAS)
        val preselectedIndex = intent.data?.getQueryParameter(BigBangActivity.EXTRA_PRESELECTED_INDEX)?.toInt()
        val saveInHistory = intent.data?.getBooleanQueryParameter(BigBangActivity.EXTRA_SAVE_IN_HISTORY, true)
        val voiceUrl = intent.data?.getQueryParameter(BigBangActivity.EXTRA_VOICE_URL)


        if (text?.isEmpty() == true) {
            mvpView?.finish()
            return
        }
        if (voiceUrl != null && voiceUrl.isNotEmpty()) { mvpView?.showVoiceBtn(voiceUrl) }

        originText = text!!
        mvpView?.resetBigBangScrollViewPos()
        mvpView?.resetMeaningViewPos()
        segmentDis?.dispose()
        segmentDis = BigBang.parseWithoutBlank(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    mvpView?.onDataInitFinished(it, preselectedIndex)
                    kanjiResultList = it
                }, { doOnError(it) })

        if(saveInHistory == true) historyDao.insert(History(text = text, alias = alias))
    }

    fun onItemClicked(index: Int, selectedByUser: Boolean) {
        if (selectedByUser) spotlight(index)
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

        onWordSelectedByUser(index)
        mvpView?.meaningText = context.getString(R.string.searching_translation)
        dictDisposable?.dispose()
        dictDisposable = searchDictHelper.searchForCombineResultAndTranslateIfNoMeaning(strForSearch, langUtils)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mvpView?.meaningText = it.getMeaning(context)
                    val readingStr = it.readingStr
                    if (readingStr.isNotEmpty() && readingStr.contains(",")) {
                        mvpView?.pronunciationText = readingStr
                    }
                }, { doOnError(it) })
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
            doOnError(e)
        }
        return true
    }

    fun onLongClickAudio() : Boolean {
        try {
            ttsHelper.play(mvpView?.activity!!, originText)
        } catch (e: Exception) {
            doOnError(e)
        }
        return true
    }

    private fun spotlight(index: Int) {
        if (appPre.hasShownWordBookTips) return
        appPre.hasShownWordBookTips = true
        mvpView?.spotlight(index)
    }

    private fun onWordSelectedByUser(index: Int) {
        if (!appPre.enableWordBook) return
        val kanjiResult = kanjiResultList?.get(index)!!
        if (!kanjiResult.baseForm.hasKanjiOrKana()) return
        
        wordDao.findByBaseForm(kanjiResult.baseForm)
                .subscribeOn(Schedulers.io())
                .map {
                    val wordId = if (it.isEmpty()) {
                        wordDao.insert(Word(baseForm = kanjiResult.baseForm))
                    } else {
                        val word = it[0]
                        wordDao.update(word.afterSearchAgain())
                        word.id
                    }
                    return@map ContextFinder.get(wordId, kanjiResultList!!, index)
                }
                .map {
                    val contextList = contextStrDao.findByWord(it.text, it.fromIndex, it.toIndex)
                    if (contextList.isEmpty()) {
                        contextStrDao.insert(it)
                    }
                }
                .subscribe({}, { doOnError(it) })
    }

}