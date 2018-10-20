package im.dacer.kata.ui.main.wordbook

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.room.dao.ContextStrDao
import im.dacer.kata.data.room.dao.WordDao
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.SnackBarHelper
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import javax.inject.Inject


@ConfigPersistent
class WordBookPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<WordBookMvp>() {

    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var appPref: MultiprocessPref
    @Inject lateinit var wordDao: WordDao
    @Inject lateinit var contextStrDao: ContextStrDao

    private var refreshWordDis: Disposable? = null
    private var wordList: List<Word>? = null
    private var showLearning = true

    val swipeListener = object : OnItemSwipeListener {

        override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
            val word = wordList?.get(pos) ?: return
            SnackBarHelper.showRedo(context, mvpView!!.getDecorView(), getStr(R.string.deleted_sth, word.baseForm), { _ ->
                contextStrDao.findByWordId(word.id).subscribe { contextStrList ->
                    contextStrList.forEach{ contextStrDao.delete(it) }
                }
                wordDao.delete(word)
            }) {
                refreshWordList()
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
        val word = wordList?.get(pos) ?: return
        if (!showLearning) {
            wordDao.update(word.markLearning())
            SnackBarHelper.showRedo(context, mvpView!!.getDecorView(),
                    getStr(R.string.mark_sth_as_learning, word.baseForm), {}) {
                wordDao.update(word.markMastered())
            }
        }
    }

    fun onClickChangeList(): Boolean {
        showLearning = !showLearning
        refreshWordList()
        return true
    }

    private fun refreshWordList() {
        mvpView?.setChangeListMenuName(if (showLearning) getStr(R.string.learning) else getStr(R.string.mastered))
        refreshWordDis?.dispose()
        refreshWordDis = getLoadWordFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { wordList ->
                    this.wordList = wordList
                    mvpView?.showWords(wordList)
                }
    }

    private fun getLoadWordFlowable(): Flowable<List<Word>> {
        return if (showLearning) {
            wordDao.loadNotMasteredFlowable()
        } else {
            wordDao.loadMastered()
        }
    }

    private fun getStr(resId: Int, vararg formatArgs: String = arrayOf()): String {
        return context.getString(resId, *formatArgs)
    }
}