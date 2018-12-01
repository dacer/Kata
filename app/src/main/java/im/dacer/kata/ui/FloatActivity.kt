package im.dacer.kata.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.model.bigbang.History
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.data.room.dao.HistoryDao
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.service.UrlAnalysisService
import im.dacer.kata.ui.base.BaseActivity
import im.dacer.kata.ui.bigbang.BigBangActivity
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.extension.isUrl
import im.dacer.kata.util.extension.timberAndToast
import im.dacer.kata.util.helper.SchemeHelper
import im.dacer.kata.util.segment.BigBang
import im.dacer.kata.view.FloatingView
import im.dacer.kata.view.KataLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_float.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Dacer on 31/01/2018.
 */
class FloatActivity : BaseActivity(), KataLayout.ItemClickListener {
    private var disposable: Disposable? = null
    private var dictDisposable: Disposable? = null
    private var sharedText: String? = null
    @Inject lateinit var appPre: MultiprocessPref
    @Inject lateinit var historyDao: HistoryDao
    @Inject lateinit var searchDictHelper: SearchDictHelper
    @Inject lateinit var langUtils: LangUtils
    @Inject lateinit var wordDao: WordDao

    override fun layoutId() = R.layout.activity_float

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        
        applyStyle()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onItemClicked(index: Int, selectedByUser: Boolean) {
        SchemeHelper.startKata(this, sharedText!!, index, saveInHistory = false)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        dictDisposable?.dispose()
    }

    @SuppressLint("InlinedApi")
    private fun handleIntent(intent: Intent) {
        var skipFloatBtn = false

        sharedText = intent.data?.getQueryParameter(BigBangActivity.EXTRA_TEXT)

        if (sharedText.isNullOrEmpty() && intent.action == Intent.ACTION_PROCESS_TEXT) {
            skipFloatBtn = true
            sharedText = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
        }

        if (sharedText.isNullOrEmpty() && intent.action == Intent.ACTION_SEND) {
            skipFloatBtn = true
            sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT)
            if (sharedText.isNullOrEmpty()) {
                sharedText = getIntent().getStringExtra(Intent.EXTRA_SUBJECT)
            }
        }

        if (sharedText.isNullOrEmpty()) {
            finish()
            return
        }

        //sharedText process finished

        sharedText = sharedText!!.trim() //remove whitespaces from the beginning and end

        if (sharedText!!.isUrl()) {
            startService(UrlAnalysisService.getIntent(this@FloatActivity, sharedText!!))
            finish()
            return
        }

        if (!sharedText!!.isFewWords() || !appPre.showFloatDialog) {
            if (skipFloatBtn) {
                SchemeHelper.startKata(this, sharedText!!)
            } else {
                val mFloatingView = FloatingView(applicationContext)
                mFloatingView.mText = sharedText
                mFloatingView.show()
            }
            finish()
            return
        }

        historyDao.insert(History(text = sharedText))
        applyData()
    }

    private fun String.isFewWords(): Boolean =
            this.count{ it == '。' } <= 1 && this.length < SchemeHelper.SHOW_FLOAT_MAX_TEXT_COUNT

    private fun applyData() {
        disposable?.dispose()
        disposable = BigBang.getSegmentParserAsync()
                .flatMap { it.parse(sharedText!!) }
                .flatMap { Observable.fromIterable(it) }
                .filter { it.baseForm.isNotBlank() }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    kataLayout.reset()
                    kataLayout.setKanjiResultData(it)
                    if (it.size == 1) {
                        loadDict(it[0])
                        saveWord(it[0])
                    }
                }, { timberAndToast(it) })
    }

    private fun saveWord(kanjiResult: KanjiResult) {
        wordDao.findByBaseForm(kanjiResult.baseForm)
                .subscribeOn(Schedulers.io())
                .map {
                    return@map if (it.isEmpty()) {
                        wordDao.insert(Word(baseForm = kanjiResult.baseForm))
                    } else {
                        val word = it[0]
                        wordDao.update(word.afterSearchAgain())
                        word.id
                    }
                }
                .subscribe({}, { Timber.e(it) })
    }

    private fun loadDict(kanjiResult: KanjiResult) {
        dividerView.visibility = View.VISIBLE
        meaningTv.text = getString(R.string.searching_translation)
        dictDisposable?.dispose()
        dictDisposable = searchDictHelper.searchForCombineResultAndTranslateIfNoMeaning(kanjiResult.strForSearch(), langUtils)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    meaningTv.text = it.getMeaning(this)
                }, { })
    }

    private fun applyStyle() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val p = window.attributes
        p.y = -dm.heightPixels
        p.alpha = 0.9f

        kataLayout.itemSpace = 2
        kataLayout.lineSpace = 10
        kataLayout.itemTextSize = appPre.getItemTextSize().toFloat()
        kataLayout.itemFuriganaTextSize = appPre.getFuriganaItemTextSize().toFloat()
        kataLayout.itemClickListener = this
    }
}