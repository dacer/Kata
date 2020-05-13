package im.dacer.kata.ui.main.wordbook

import android.view.View
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.model.bigbang.WordWithMeaning
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.ui.base.MvpView

interface WordBookMvp : MvpView {
    fun addWordWithMeaning(word: WordWithMeaning)
    fun getWordWithMeaning(index: Int): WordWithMeaning?
    fun getDecorView(): View
    fun setChangeListMenuName(name: String)
    fun showFlashcardBtn(show: Boolean)
    fun setLoading(loading: Boolean)
}