package im.dacer.kata.ui.flashcard

import android.os.Bundle
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.ui.base.BaseTransparentSwipeActivity
import kotlinx.android.synthetic.main.activity_flashcard.*
import javax.inject.Inject

class FlashcardActivity : BaseTransparentSwipeActivity(), FlashcardMvp {
    @Inject lateinit var flashcardPresenter: FlashcardPresenter
    val adapter by lazy { FlashcardAdapter(this) }

    override fun layoutId() = R.layout.activity_flashcard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        flashcardPresenter.attachView(this)
        cardStackView.setCardEventListener(flashcardPresenter)
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

}
