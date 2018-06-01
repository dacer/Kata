package im.dacer.kata.ui.main.inbox

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.baoyz.treasure.Treasure
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import im.dacer.kata.Config
import im.dacer.kata.R
import im.dacer.kata.core.data.HistoryDbHelper
import im.dacer.kata.core.data.HistoryHelper
import im.dacer.kata.core.data.MultiprocessPref
import im.dacer.kata.core.model.History
import im.dacer.kata.core.util.SchemeHelper
import im.dacer.kata.data.DictImporter
import im.dacer.kata.widget.PopupView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class InboxPresenter(val context: Context, private val inboxMvp: InboxMvp) : PopupView.PopupListener {

    private var nothingHappenedCountdown: Disposable? = null
    private var showGoYoutubeCountdown: Disposable? = null
    private val treasure by lazy { Treasure.get(context, Config::class.java) }
    private val appPref by lazy { MultiprocessPref(context) }
    private var refreshHistoryDis: Disposable? = null

    private val historyDbHelper by lazy { im.dacer.kata.core.data.HistoryDbHelper(context) }
    private val db by lazy { historyDbHelper.readableDatabase }
    private var historyList: List<im.dacer.kata.core.model.History>? = null
    val swipeListener = object: OnItemSwipeListener {
        override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
            val history = historyList?.get(pos)
            if (history != null) {
                im.dacer.kata.core.data.HistoryHelper.delete(db, history.id())
                Snackbar.make(inboxMvp.getDecorView(), context.getString(R.string.deleted_sth, history.text()), Snackbar.LENGTH_SHORT)
                        .setAction(R.string.redo, {
                            im.dacer.kata.core.data.HistoryHelper.save(db, history.text()!!)
                            refreshHistoryList()
                        })
                        .show()
            }
        }

        override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}
        override fun onItemSwipeMoving(canvas: Canvas?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, isCurrentlyActive: Boolean) {}

    }

    fun showLyricMenu(): Boolean = treasure.showLyricBtn()

    fun onResume() {
        if (!treasure.hasShownGoYoutube()) {
            showGoYoutubeCountdown = Observable.timer(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        inboxMvp.showGoYoutubeView()
                        treasure.setHasShownGoYoutube(true)
                    }
        }
        refreshHistoryList()
    }

    fun onStop() {
        nothingHappenedCountdown?.dispose()
        refreshHistoryDis?.dispose()
        showGoYoutubeCountdown?.dispose()
    }

    fun onDestroy() {
        db.close()
    }

    fun refreshHistoryList() {
        refreshHistoryDis?.dispose()
        if (appPref.tutorialFinished && treasure.cacheMax > 0) {
            refreshHistoryDis = Observable.fromCallable { im.dacer.kata.core.data.HistoryHelper.get(db, treasure.cacheMax) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        historyList = it
                        if (historyList?.isNotEmpty() == true) {
                            inboxMvp.showHistory(historyList!!)
                        }
                    }
        } else {
            inboxMvp.showHistory(null)
        }
    }

    fun importDictDb() {
        val dbImporter = DictImporter(context)
        if (!dbImporter.isDataBaseExists || !treasure.isDatabaseImported) {
            inboxMvp.setBigbangTipTv(R.string.initializing_database)
            Observable.fromCallable{ dbImporter.importDataBaseFromAssets() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        inboxMvp.setBigbangTipTv(R.string.bigbang_hold_tip)
                        treasure.isDatabaseImported = true
                    }, { inboxMvp.catchError(it) })
        }
    }

    fun onHistoryClicked(position: Int) {
        val history = historyList?.get(position)
        history?.run {
            SchemeHelper.startKata(context, this.text()!!, saveInHistory =  false)
        }
    }

    fun onHistoryLongClicked(activity: Activity, index: Int): Boolean {
        val history = historyList?.get(index)!!

        MaterialDialog.Builder(activity)
                .items(getLongClickItems(context))
                .itemsCallback { _, _, pos, _ ->
                    when (pos) {
                        0 -> starHistory(index, history)
                        1 -> setHistoryAlias(activity, index, history)
                    }
                }
                .show()

        return true
    }

    private fun starHistory(index: Int, h: im.dacer.kata.core.model.History) {
        val isStar = h.star() == true
        inboxMvp.updateHistory(index, im.dacer.kata.core.model.History.newInstance(h.id(), h.text(), h.alias(), !isStar, h.createdAt()))
        im.dacer.kata.core.data.HistoryHelper.update(db, h.id(), h.alias(), !isStar)
    }

    private fun setHistoryAlias(activity: Activity, index: Int, h: im.dacer.kata.core.model.History) {
        MaterialDialog.Builder(activity)
                .input(context.getString(R.string.set_alias), h.alias(), true,
                        { _, char ->
                            inboxMvp.updateHistory(index, im.dacer.kata.core.model.History.newInstance(h.id(), h.text(), char.toString(), h.star(), h.createdAt()))
                            im.dacer.kata.core.data.HistoryHelper.update(db, h.id(), char.toString(), h.star())
                        })
                .show()
    }

    override fun onPopupClicked() {
        val service = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        service.primaryClip = ClipData.newPlainText("", inboxMvp.getClipTvText())
        Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()

        nothingHappenedCountdown = Observable.timer(8, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { inboxMvp.showNothingHappenedView()}
    }

    companion object {
        fun getLongClickItems(c: Context): ArrayList<String> =
                arrayListOf(c.getString(R.string.star), c.getString(R.string.set_alias))
    }
}