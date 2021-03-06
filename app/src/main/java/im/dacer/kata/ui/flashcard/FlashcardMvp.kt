package im.dacer.kata.ui.flashcard

import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.ui.base.MvpView

interface FlashcardMvp : MvpView {
    fun setWordList(wordList: Array<Word>)
    fun showCongratulations()
    fun showEmpty()
    fun allCardsSwiped(): Boolean
    fun getLastWord(): Word?
}