package im.dacer.kata.ui.main.wordbook

import android.content.Context
import android.graphics.Canvas
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.ui.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@ConfigPersistent
class WordBookPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<WordBookMvp>() {

    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var appPref: MultiprocessPref
    @Inject lateinit var wordDao: WordDao

    private var refreshWordDis: Disposable? = null
    private var wordList: List<Word>? = null

    val swipeListener = object: OnItemSwipeListener {

        override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
            val word = wordList?.get(pos)
            if (word != null) {
                wordDao.delete(word)
                Snackbar.make(mvpView!!.getDecorView(), context.getString(R.string.deleted_sth, word.baseForm), Snackbar.LENGTH_SHORT)
                        .setAction(R.string.redo) {
                            wordDao.insert(word)
                            refreshWordList()
                        }
                        .show()
            }
        }

        override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}
        override fun onItemSwipeMoving(canvas: Canvas?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, isCurrentlyActive: Boolean) {}

    }

    fun onResume() {
        refreshWordList()
    }

    override fun detachView() {
        super.detachView()
        refreshWordDis?.dispose()
    }

    fun onWordClicked(pos: Int) {

    }

    fun refreshWordList() {
        refreshWordDis?.dispose()
        refreshWordDis = wordDao.loadNotMasteredFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ wordList ->
                    this.wordList = wordList
                    mvpView?.showWords(wordList)
                }
    }

}