package im.dacer.kata.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.model.bigbang.History
import im.dacer.kata.data.room.dao.HistoryDao
import im.dacer.kata.service.UrlAnalysisService
import im.dacer.kata.ui.base.BaseActivity
import im.dacer.kata.ui.bigbang.BigBangActivity
import im.dacer.kata.util.extension.findUrl
import im.dacer.kata.util.extension.timberAndToast
import im.dacer.kata.util.extension.toKanjiResultList
import im.dacer.kata.util.helper.SchemeHelper
import im.dacer.kata.util.segment.BigBang
import im.dacer.kata.view.FloatingView
import im.dacer.kata.view.KataLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_float.*
import javax.inject.Inject

/**
 * Created by Dacer on 31/01/2018.
 */
class FloatActivity : BaseActivity(), KataLayout.ItemClickListener {
    private var disposable: Disposable? = null
    private var sharedText: String? = null
    @Inject lateinit var appPre: MultiprocessPref
    @Inject lateinit var historyDao: HistoryDao

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

    override fun onItemClicked(index: Int) {
        SchemeHelper.startKata(this, sharedText!!, index)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
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

        sharedText!!.findUrl()?.run {
            startService(UrlAnalysisService.getIntent(this@FloatActivity, this))
            finish()
            return
        }

        if (!sharedText!!.isFewWords()) {
            if (skipFloatBtn) {
                SchemeHelper.startKata(this, sharedText!!, 0)
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
                .flatMap { Observable.fromIterable(it.toKanjiResultList()) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    kataLayout.reset()
                    kataLayout.setKanjiResultData(it)
                }, { timberAndToast(it) })
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