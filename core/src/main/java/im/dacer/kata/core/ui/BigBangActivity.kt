package im.dacer.kata.core.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.view.View
import com.atilika.kuromoji.ipadic.Token
import im.dacer.kata.SearchEngine
import im.dacer.kata.core.BigBang
import im.dacer.kata.core.R
import im.dacer.kata.core.action.SearchAction
import im.dacer.kata.core.data.HistoryHelper
import im.dacer.kata.core.data.JMDictDbHelper
import im.dacer.kata.core.data.MultiprocessPref
import im.dacer.kata.core.data.SearchHelper
import im.dacer.kata.core.extension.getSubtitle
import im.dacer.kata.core.extension.strForSearch
import im.dacer.kata.core.extension.timberAndToast
import im.dacer.kata.core.model.DictEntry
import im.dacer.kata.core.model.DictReading
import im.dacer.kata.core.util.LangUtils
import im.dacer.kata.core.util.TTSHelper
import im.dacer.kata.core.view.KataLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_big_bang.*


class BigBangActivity : AppCompatActivity(), KataLayout.ItemClickListener {

    private var kanjiResultList: List<Token>? = null
    private var dictDb: SQLiteDatabase? = null
    private var segmentDis: Disposable? = null
    private var searchHelper: SearchHelper? = null
    private var dictDisposable: Disposable? = null
    private var currentSelectedToken: Token? = null
    private var searchAction: SearchAction? = null
    private var ttsHelper: TTSHelper? = null
    private val appPre by lazy { MultiprocessPref(this) }
    private val langUtils by lazy { LangUtils(appPre) }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_bang)
        ttsHelper = TTSHelper(applicationContext)
        kataLayout.itemSpace = appPre.getItemSpace()
        kataLayout.lineSpace = appPre.getLineSpace()
        kataLayout.itemTextSize = appPre.getItemTextSize().toFloat()
        kataLayout.itemFuriganaTextSize = appPre.getFuriganaItemTextSize().toFloat()
        kataLayout.itemClickListener = this
        kataLayout.showFurigana(!appPre.hideFurigana)
        appPre.tutorialFinished = true
        loadingProgressBar.indeterminateDrawable.setColorFilter(Color.parseColor("#EEEEEE"), PorterDuff.Mode.MULTIPLY)
        searchAction = SearchEngine.getDefaultSearchAction(this)

        handleIntent(intent)
        searchBtn.setOnClickListener { onClickSearch() }
        audioBtn.setOnClickListener { onClickAudio() }
        searchBtn.setOnLongClickListener {
            val popup = PopupMenu(this, it)
            it.setOnTouchListener(popup.dragToOpenListener)
            SearchEngine.getSupportSearchEngineList().forEach { popup.menu.add(it) }
            popup.setOnMenuItemClickListener {
                searchAction = SearchEngine.getSearchAction(it.title.toString())
                onClickSearch()
            }
            popup.show()
            true
        }
        eyeBtn.setOnClickListener {
            val showFurigana = !kataLayout.showFurigana
            appPre.hideFurigana = !showFurigana
            kataLayout.showFurigana(showFurigana)
            refreshIconStatus()
        }
    }

    private fun onClickSearch() : Boolean {
        currentSelectedToken?.run { searchAction!!.start(baseContext, this.strForSearch()) }
        return true
    }

    private fun onClickAudio() : Boolean {
        try {
            currentSelectedToken?.run { ttsHelper?.play(this@BigBangActivity, this.baseForm) }
        } catch (e: Exception) {
            timberAndToast(e)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        refreshIconStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        dictDb?.close()
        ttsHelper?.onDestroy()
        segmentDis?.dispose()
        dictDisposable?.dispose()
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClicked(index: Int) {
        currentSelectedToken = kanjiResultList?.get(index)
        val strForSearch: String

        pronunciationTv.visibility = View.GONE
        if (currentSelectedToken?.isKnown == true) {
            descTv.text = "[${currentSelectedToken?.baseForm}] ${currentSelectedToken?.getSubtitle()}"
            meaningTv.text = ""
            strForSearch = currentSelectedToken!!.baseForm

        } else {
            descTv.text = currentSelectedToken?.surface
            strForSearch = currentSelectedToken?.surface ?: ""
        }

        dictDisposable?.dispose()
        dictDisposable = Observable.fromCallable{ searchHelper!!.search(strForSearch) }
                .flatMap {
                    Observable.zip(
                            dealWithDictEntryList(it.dictEntryList),
                            dealWithDictReadingList(it.dictReadingList),
                            BiFunction<String, String, CombinedResult> { meaningStr, readingStr -> CombinedResult(meaningStr, readingStr) }
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val meaningStr = it.meaningStr
                    meaningTv.text = if (meaningStr.isBlank()) {
                        getString(R.string.not_found_error, strForSearch)
                    } else { meaningStr }

                    val readingStr = it.readingStr
                    if (readingStr.isNotEmpty() && readingStr.contains(",")) {
                        pronunciationTv.visibility = View.VISIBLE
                        pronunciationTv.text = readingStr
                    }
                }, { timberAndToast(it) })
    }

    private fun dealWithDictEntryList(dictEntryList: List<DictEntry>): Observable<String> {
        return Observable.fromIterable(dictEntryList)
                .flatMap { langUtils.fetchTranslation(it) }
                .toList()
                .map { it.joinToString("\n\n") { "· $it" } }
                .toObservable()
    }

    private fun dealWithDictReadingList(dictReadingList: List<DictReading>?): Observable<String> {
        return Observable.fromCallable { dictReadingList?.joinToString(",") {it.reading() ?: ""} ?: "" }
    }

    private data class CombinedResult(val meaningStr: String, val readingStr: String)

    private fun refreshIconStatus() {
        eyeBtn.text = if (kataLayout.showFurigana) "{gmd-visibility}" else "{gmd-visibility-off}"
    }

    private fun handleIntent(intent: Intent) {
        val text = intent.data.getQueryParameter(EXTRA_TEXT)
        val alias = intent.data.getQueryParameter(EXTRA_ALIAS)
        val preselectedIndex = intent.data.getQueryParameter(PRESELECTED_INDEX)?.toInt()
        val saveInHistory = intent.data.getBooleanQueryParameter(SAVE_IN_HISTORY, true)

        if (text.isEmpty()) {
            finish()
            return
        }
        meaningScrollView.smoothScrollTo(0,0)
        bigBangScrollView.smoothScrollTo(0,0)
        dictDb = JMDictDbHelper(this).readableDatabase
        searchHelper = SearchHelper(dictDb!!)

        segmentDis?.dispose()
        segmentDis = BigBang.getSegmentParserAsync()
                .flatMap { it.parse(text) }
                .flatMap { Observable.fromIterable(it) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    loadingProgressBar.visibility = View.GONE
                    kataLayout.reset()
                    resetTopLayout()
                    kanjiResultList = it
                    kataLayout.setTokenData(it)
                    preselectedIndex?.let { kataLayout.select(it) }

                }, { timberAndToast(it) })

        if(saveInHistory) HistoryHelper.saveAsync(this, text, alias)
    }

    private fun resetTopLayout() {
        descTv.text = ""
        meaningTv.text = ""
        pronunciationTv.visibility = View.GONE
    }

    companion object {
        const val EXTRA_TEXT = "extra_text"
        const val EXTRA_ALIAS = "extra_alias"
        const val PRESELECTED_INDEX = "preselected_index"
        const val SAVE_IN_HISTORY = "save_in_history"
    }

}
