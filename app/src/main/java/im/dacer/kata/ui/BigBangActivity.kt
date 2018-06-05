package im.dacer.kata.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.PopupMenu
import android.util.Property
import android.view.View
import com.atilika.kuromoji.ipadic.Token
import im.dacer.kata.R
import im.dacer.kata.data.local.HistoryHelper
import im.dacer.kata.data.local.JMDictDbHelper
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.model.bigbang.DictEntry
import im.dacer.kata.data.model.bigbang.DictReading
import im.dacer.kata.ui.base.BaseSwipeActivity
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.engine.SearchEngine
import im.dacer.kata.util.extension.getSubtitle
import im.dacer.kata.util.extension.strForSearch
import im.dacer.kata.util.extension.timberAndToast
import im.dacer.kata.util.helper.TTSHelper
import im.dacer.kata.util.segment.BigBang
import im.dacer.kata.view.KataLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_big_bang.*
import qiu.niorgai.StatusBarCompat
import javax.inject.Inject


class BigBangActivity : BaseSwipeActivity(), KataLayout.ItemClickListener, View.OnSystemUiVisibilityChangeListener {

    private var kanjiResultList: List<Token>? = null
    private var dictDb: SQLiteDatabase? = null
    private var segmentDis: Disposable? = null
    private var searchDictHelper: SearchDictHelper? = null
    private var dictDisposable: Disposable? = null
    private var currentSelectedToken: Token? = null
    private var searchAction: im.dacer.kata.util.action.SearchAction? = null

    @Inject lateinit var appPre: MultiprocessPref
    @Inject lateinit var langUtils: LangUtils
    @Inject lateinit var ttsHelper: TTSHelper

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun layoutId() = R.layout.activity_big_bang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)

        StatusBarCompat.translucentStatusBar(this, true)
        kataLayout.itemSpace = appPre.getItemSpace()
        kataLayout.lineSpace = appPre.getLineSpace()
        kataLayout.itemTextSize = appPre.getItemTextSize().toFloat()
        kataLayout.itemFuriganaTextSize = appPre.getFuriganaItemTextSize().toFloat()
        kataLayout.itemClickListener = this
        kataLayout.showFurigana(!appPre.hideFurigana)
        appPre.tutorialFinished = true
        loadingProgressBar.indeterminateDrawable.setColorFilter(Color.parseColor("#EEEEEE"), PorterDuff.Mode.MULTIPLY)
        searchAction = SearchEngine.getDefaultSearchAction(this)
        window.decorView.setOnSystemUiVisibilityChangeListener(this)

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
        bigBangScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val isDown = scrollY - oldScrollY > 0
            if (isDown) {
                if (!systemUiIsHidden) hideSystemUI()
            } else {
                if (systemUiIsHidden) showSystemUI()
            }
        })
    }

    private var topPaddingAnim: ObjectAnimator? = null
    private var systemUiIsHidden = false


    override fun onSystemUiVisibilityChange(visibility: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        systemUiIsHidden = (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) != 0
        if (topPaddingView.height == 0 && !systemUiIsHidden) {
            showSystemUI()
        }
    }

    override fun hideSystemUI() {
        topPaddingAnim?.cancel()
        topPaddingAnim = ObjectAnimator.ofInt(topPaddingView, HeightProperty(),
                topPaddingView.height, 0)
                .setDuration(UI_ANIM_DURATION)
        topPaddingAnim?.start()
        super.hideSystemUI()
    }

    override fun showSystemUI() {
        topPaddingAnim?.cancel()
        topPaddingAnim = ObjectAnimator.ofInt(topPaddingView, HeightProperty(),
                topPaddingView.height, resources.getDimension(R.dimen.tool_bar_top_padding).toInt())
                .setDuration(UI_ANIM_DURATION)
        topPaddingAnim?.start()
        super.showSystemUI()
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
        ttsHelper.onDestroy()
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
        dictDisposable = Observable.fromCallable{ searchDictHelper!!.search(strForSearch) }
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
        searchDictHelper = SearchDictHelper(dictDb!!)

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

        const val UI_ANIM_DURATION = 300L
    }


    internal inner class HeightProperty : Property<View, Int>(Int::class.java, "height") {

        override operator fun get(view: View): Int? {
            return view.height
        }

        override operator fun set(view: View, value: Int?) {
            view.layoutParams.height = value!!
            view.layoutParams = view.layoutParams
        }
    }
}
