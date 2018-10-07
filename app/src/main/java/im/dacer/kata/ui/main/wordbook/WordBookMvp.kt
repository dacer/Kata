package im.dacer.kata.ui.main.wordbook

import android.view.View
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.ui.base.MvpView

interface WordBookMvp : MvpView {
    fun showWords(wordList: List<Word>?)
    fun getDecorView(): View
}