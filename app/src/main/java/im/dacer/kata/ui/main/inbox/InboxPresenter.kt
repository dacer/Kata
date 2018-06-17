package im.dacer.kata.ui.main.inbox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import im.dacer.kata.R
import im.dacer.kata.data.DictImporter
import im.dacer.kata.data.local.HistoryDbHelper
import im.dacer.kata.data.local.HistoryHelper
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.data.model.bigbang.History
import im.dacer.kata.injection.ApplicationContext
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.helper.SchemeHelper
import im.dacer.kata.view.PopupView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ConfigPersistent
class InboxPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<InboxMvp>(), PopupView.PopupListener {

    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var appPref: MultiprocessPref

    private var nothingHappenedCountdown: Disposable? = null
    private var showGoYoutubeCountdown: Disposable? = null
    private var refreshHistoryDis: Disposable? = null

    private val historyDbHelper by lazy { HistoryDbHelper(context) }
    private val db by lazy { historyDbHelper.readableDatabase }
    private var historyList: List<History>? = null


    val swipeListener = object: OnItemSwipeListener {
        override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
            val history = historyList?.get(pos)
            if (history != null) {
                HistoryHelper.delete(db, history.id())
                Snackbar.make(mvpView!!.getDecorView(), context.getString(R.string.deleted_sth, history.text()), Snackbar.LENGTH_SHORT)
                        .setAction(R.string.redo, {
                            HistoryHelper.save(db, history.text()!!)
                            refreshHistoryList()
                        })
                        .show()
            }
        }

        override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}
        override fun onItemSwipeMoving(canvas: Canvas?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, isCurrentlyActive: Boolean) {}

    }

    fun onResume() {
        if (!settingUtility.hasShownGoYoutube) {
            showGoYoutubeCountdown = Observable.timer(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mvpView?.showGoYoutubeView()
                        settingUtility.hasShownGoYoutube = true
                    }
        }
        refreshHistoryList()
    }

    fun onStop() {
        nothingHappenedCountdown?.dispose()
        refreshHistoryDis?.dispose()
        showGoYoutubeCountdown?.dispose()
    }

    override fun detachView() {
        super.detachView()
        db.close()
    }

    fun refreshHistoryList() {
        refreshHistoryDis?.dispose()
        if (appPref.tutorialFinished && settingUtility.cacheMax > 0) {
            refreshHistoryDis = Observable.fromCallable { HistoryHelper.get(db, settingUtility.cacheMax) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        historyList = it
                        if (historyList?.isNotEmpty() == true) {
                            mvpView?.showHistory(historyList!!)
                        }
                    }
        } else {
            mvpView?.showHistory(null)
        }
    }

    @SuppressLint("CheckResult")
    fun importDictDb() {
        val dbImporter = DictImporter(context)
        if (!dbImporter.isDataBaseExists || !settingUtility.isDatabaseImported) {
            mvpView?.setBigbangTipTv(R.string.initializing_database)
            Observable.fromCallable{ dbImporter.importDataBaseFromAssets() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        mvpView?.setBigbangTipTv(R.string.bigbang_hold_tip)
                        settingUtility.isDatabaseImported = true
                    }, { mvpView?.catchError(it) })
        }
    }

    fun onHistoryClicked(position: Int) {
        val history = historyList?.get(position)
        history?.run {
            SchemeHelper.startKata(context, this.text()!!, saveInHistory =  false, activity = mvpView?.activity)
        }

    }

    fun onHistoryLongClicked(activity: Activity, index: Int): Boolean {
        val history = historyList?.get(index)!!
        MaterialDialog.Builder(activity)
                .items(getHistoryLongClickItems(history))
                .itemsCallback { _, _, pos, _ ->
                    when (pos) {
                        0 -> starHistory(index, history)
                        1 -> setHistoryAlias(activity, index, history)
                    }
                }
                .show()

        return true
    }

    private fun starHistory(index: Int, h: History) {
        val isStar = h.star() == true
        mvpView?.updateHistory(index, History.newInstance(h.id(), h.text(), h.alias(), !isStar, h.createdAt()))
        HistoryHelper.update(db, h.id(), h.alias(), !isStar)
    }

    private fun setHistoryAlias(activity: Activity, index: Int, h: History) {
        MaterialDialog.Builder(activity)
                .input(context.getString(R.string.set_alias), h.alias(), true,
                        { _, char ->
                            mvpView?.updateHistory(index, History.newInstance(h.id(), h.text(), char.toString(), h.star(), h.createdAt()))
                            HistoryHelper.update(db, h.id(), char.toString(), h.star())
                        })
                .show()
    }

    override fun onPopupClicked() {
        val service = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        service.primaryClip = ClipData.newPlainText("", mvpView?.getClipTvText())
        Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()

//        nothingHappenedCountdown = Observable.timer(8, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { mvpView?.showNothingHappenedView()}
    }

    private fun getHistoryLongClickItems(history: History?): ArrayList<String> =
            arrayListOf(context.getString(if (history?.star() != true) { R.string.star } else { R.string.unstar }),
                    context.getString(R.string.set_alias))
    companion object {
    }
}