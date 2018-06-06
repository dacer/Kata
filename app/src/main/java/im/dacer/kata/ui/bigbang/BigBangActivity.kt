package im.dacer.kata.ui.bigbang

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.PopupMenu
import android.util.Property
import android.view.View
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.ui.base.BaseSwipeActivity
import im.dacer.kata.util.engine.SearchEngine
import im.dacer.kata.view.KataLayout
import im.dacer.kata.view.MyScrollView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_big_bang.*
import qiu.niorgai.StatusBarCompat
import javax.inject.Inject


class BigBangActivity : BaseSwipeActivity(), BigbangMvp, KataLayout.ItemClickListener, View.OnSystemUiVisibilityChangeListener {
    private var dictDisposable: Disposable? = null

    @Inject lateinit var bigbangPresenter: BigbangPresenter
    @Inject lateinit var appPre: MultiprocessPref


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun layoutId() = R.layout.activity_big_bang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        bigbangPresenter.attachView(this)

        StatusBarCompat.translucentStatusBar(this, true)
        kataLayout.itemSpace = appPre.getItemSpace()
        kataLayout.lineSpace = appPre.getLineSpace()
        kataLayout.itemTextSize = appPre.getItemTextSize().toFloat()
        kataLayout.itemFuriganaTextSize = appPre.getFuriganaItemTextSize().toFloat()
        kataLayout.itemClickListener = this
        kataLayout.showFurigana(!appPre.hideFurigana)
        appPre.tutorialFinished = true
        loadingProgressBar.indeterminateDrawable.setColorFilter(Color.parseColor("#EEEEEE"), PorterDuff.Mode.MULTIPLY)
        window.decorView.setOnSystemUiVisibilityChangeListener(this)

        handleIntent(intent)
        searchBtn.setOnClickListener { bigbangPresenter.onClickSearch() }
        audioBtn.setOnClickListener { bigbangPresenter.onClickAudio() }
        searchBtn.setOnLongClickListener {
            val popup = PopupMenu(this, it)
            it.setOnTouchListener(popup.dragToOpenListener)
            SearchEngine.getSupportSearchEngineList().forEach { popup.menu.add(it) }
            popup.setOnMenuItemClickListener { bigbangPresenter.changeAndFireSearchAction(it) }
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
            if (Math.abs(scrollY - oldScrollY) < 3) return@OnScrollChangeListener
            if (isDown) {
                if (Math.abs(scrollY) < getDimen(R.dimen.big_bang_meaning_height) - getDimen(R.dimen.big_bang_meaning_mini_height)) return@OnScrollChangeListener
                if (!systemUiIsHidden) hideSystemUI()
            } else {
                if (systemUiIsHidden) showSystemUI()
            }
        })
        bigBangScrollView.overScrollListener = object : MyScrollView.OverScrollListener {
            override fun onOverScroll(topOverScroll: Boolean) {
                if (topOverScroll) {
                    if (systemUiIsHidden) showSystemUI()
                } else {
                    if (!systemUiIsHidden) hideSystemUI()
                }
            }
        }
    }

    private var changeUiAnim: AnimatorSet? = null
    private var systemUiIsHidden = false


    override fun onSystemUiVisibilityChange(visibility: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        systemUiIsHidden = (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) != 0
        if (topPaddingView.height == 0 && !systemUiIsHidden) {
            showSystemUI()
        }
    }

    override fun hideSystemUI() {
        systemUiIsHidden = true
        changeUiAnim?.cancel()
        changeUiAnim = AnimatorSet()
        changeUiAnim?.playTogether(
                ObjectAnimator.ofInt(topPaddingView, HeightProperty(), topPaddingView.height, 0),
                ObjectAnimator.ofInt(meaningScrollView, HeightProperty(), meaningScrollView.height, getDimen(R.dimen.big_bang_meaning_mini_height)))
        changeUiAnim?.duration = UI_ANIM_DURATION
        changeUiAnim?.start()
        resetMeaningViewPos()
        super.hideSystemUI()
    }

    override fun showSystemUI() {
        systemUiIsHidden = false
        changeUiAnim?.cancel()
        changeUiAnim = AnimatorSet()
        changeUiAnim?.playTogether(
                ObjectAnimator.ofInt(topPaddingView, HeightProperty(), topPaddingView.height, getDimen(R.dimen.tool_bar_top_padding)),
                ObjectAnimator.ofInt(meaningScrollView, HeightProperty(), meaningScrollView.height, getDimen(R.dimen.big_bang_meaning_height)))
        changeUiAnim?.duration = UI_ANIM_DURATION
        changeUiAnim?.start()

        super.showSystemUI()
    }

    private fun getDimen(dimenResId: Int) : Int {
        return resources.getDimensionPixelSize(dimenResId)
    }

    override fun onDataInitFinished(list: List<KanjiResult>, preselectedIndex: Int?) {
        loadingProgressBar.visibility = View.GONE
        kataLayout.reset()
        resetTopLayout()
        kataLayout.setKanjiResultData(list)
        preselectedIndex?.let { kataLayout.select(it) }
    }

    override fun onResume() {
        super.onResume()
        refreshIconStatus()
    }

    override fun onDestroy() {
        dictDisposable?.dispose()
        bigbangPresenter.detachView()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClicked(index: Int) {
        bigbangPresenter.onItemClicked(index)
    }

    private fun refreshIconStatus() {
        eyeBtn.text = if (kataLayout.showFurigana) "{gmd-visibility}" else "{gmd-visibility-off}"
    }

    private fun handleIntent(intent: Intent) {
        bigbangPresenter.handIntent(intent)
    }

    private fun resetTopLayout() {
        descTv.text = ""
        meaningTv.text = ""
        pronunciationTv.visibility = View.GONE
    }

    override val activity: Activity = this

    override var descText: String? = ""
        set(value) { descTv.text = value }

    override var meaningText: String = ""
        set(value) { meaningTv.text = value }

    override var pronunciationText: String? = ""
        set(value) {
            if (value.isNullOrEmpty()) {
                pronunciationTv.visibility = View.GONE
            } else {
                pronunciationTv.text = value
                pronunciationTv.visibility = View.VISIBLE
            }
        }

    override fun resetMeaningViewPos() {
        if (meaningScrollView.scrollY != 0) meaningScrollView.smoothScrollTo(0,0)
    }

    override fun resetBigBangScrollViewPos() {
        if (bigBangScrollView.scrollY != 0) bigBangScrollView.smoothScrollTo(0,0)
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
