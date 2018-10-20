package im.dacer.kata.ui.flashcard

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.pixplicity.sharp.Sharp
import im.dacer.kata.R
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.room.dao.ContextStrDao
import im.dacer.kata.ui.base.BaseTransparentSwipeActivity
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.extension.getNavBarHeight
import im.dacer.kata.util.extension.onRendered
import im.dacer.kata.util.extension.setPaddingBottom
import kotlinx.android.synthetic.main.activity_flashcard.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import javax.inject.Inject

class FlashcardActivity : BaseTransparentSwipeActivity(), FlashcardMvp {
    @Inject lateinit var flashcardPresenter: FlashcardPresenter
    @Inject lateinit var langUtils: LangUtils
    @Inject lateinit var contextStrDao: ContextStrDao
    @Inject lateinit var searchDictHelper: SearchDictHelper
    private val adapter by lazy { FlashcardAdapter(this, searchDictHelper, langUtils, contextStrDao) }

    override fun layoutId() = R.layout.activity_flashcard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        flashcardPresenter.attachView(this)
        cardStackView.setCardEventListener(flashcardPresenter)

        cardStackView.onRendered {
            val navBarHeight = getNavBarHeight()
            if (navBarHeight > 0) {
                cardStackView.setPaddingBottom(navBarHeight)
            }
        }
        Sharp.loadResource(resources, R.raw.fireworks).into(fireworksIv)
    }

    override fun onDestroy() {
        flashcardPresenter.detachView()
        super.onDestroy()
    }

    override fun setWordList(wordList: Array<Word>) {
        adapter.clear()
        adapter.addAll(*wordList)
        cardStackView.setAdapter(adapter)
    }

    override fun allCardsSwiped(): Boolean {
        return cardStackView.topIndex == adapter.count
    }

    override fun getLastWord(): Word? {
        if (cardStackView.topIndex <= 0 ) return null
        return adapter.getItem(cardStackView.topIndex - 1)
    }

    override fun showEmpty() {
        showViewWithAnim(emptyView)
    }

    override fun showCongratulations() {
        showViewWithAnim(congratulationView)
        konfettiView.build()
                .addColors(HAPPY_COLORS.map { ContextCompat.getColor(this, it) })
                .setDirection(0.0, 359.0)
                .setSpeed(4f, 7f)
                .setFadeOutEnabled(true)
                .setTimeToLive(3000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(Size(12), Size(16, 6f))
                .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
                .streamFor(100, 5000L)
    }

    private fun showViewWithAnim(view: View) {
        view.visibility = View.VISIBLE
        view.alpha = 0.0f
        view.animate()
                .setDuration(300)
                .alpha(1.0f)
    }

    companion object {
        val HAPPY_COLORS = intArrayOf(R.color.dk_cyan, R.color.dk_green, R.color.dk_red, R.color.dk_blue)
    }
}
