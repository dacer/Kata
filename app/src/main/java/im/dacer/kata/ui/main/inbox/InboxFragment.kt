package im.dacer.kata.ui.main.inbox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.History
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.about.AboutActivity
import im.dacer.kata.ui.base.BaseFragment
import im.dacer.kata.util.extension.getNavBarHeight
import im.dacer.kata.util.extension.timberAndToast
import kotlinx.android.synthetic.main.fragment_inbox.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

class InboxFragment : BaseFragment(), InboxMvp {
    @Inject lateinit var inboxPresenter: InboxPresenter
    private val historyAdapter = HistoryAdapter()

    override fun layoutId() = R.layout.fragment_inbox
    override val activity: Activity? get() =  getActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        inboxPresenter.attachView(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popupView.listener = inboxPresenter
        inboxPresenter.importDictDb()
        historyRecyclerView.layoutManager = LinearLayoutManager(context)

        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(historyAdapter)
        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        historyAdapter.bindToRecyclerView(historyRecyclerView)
        itemTouchHelper.attachToRecyclerView(historyRecyclerView)
        historyAdapter.setOnItemClickListener { _, _, pos -> inboxPresenter.onHistoryClicked(pos)}
        historyAdapter.setOnItemLongClickListener { _, _, pos -> inboxPresenter.onHistoryLongClicked(activity!!, pos) }
        historyAdapter.setEmptyView(R.layout.empty_recycler_view)
        val bottomView = layoutInflater.inflate(R.layout.item_history_bottom, historyRecyclerView.parent as ViewGroup, false)
        historyAdapter.setFooterView(bottomView)
        historyAdapter.enableSwipeItem()
        historyAdapter.setOnItemSwipeListener(inboxPresenter.swipeListener)

        clipTv.setOnLongClickListener {
            popupView.show(Point((clipTv.width / 2), clipTv.y.toInt()- bigbangTipTv.height))
            return@setOnLongClickListener true
        }
        permissionErrorLayout.setOnClickListener {
            val requestIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${activity?.packageName}"))
            ListenClipboardService.stop(context!!)
            try {
                startActivityForResult(requestIntent, REQUEST_CODE_OVERLAY_PERMISSION)
            } catch (e: Throwable) {
                toast(getString(R.string.cannot_open_overlay_permission_settings))
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        inboxPresenter.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val canDraw = Settings.canDrawOverlays(activity)
            permissionErrorLayout.visibility = if (canDraw) View.GONE else View.VISIBLE
            permissionBottomMargin.layoutParams.height = activity!!.getNavBarHeight()
            permissionBottomMargin.layoutParams = permissionBottomMargin.layoutParams
        }
//        nothingHappenedView.visibility = View.GONE
        goToYoutubeView.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        inboxPresenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        inboxPresenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
//            inboxPresenter.restartListenService()
        }
    }

    override fun showNothingHappenedView() {
        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        nothingHappenedView.visibility = View.VISIBLE
        nothingHappenedView.startAnimation(slideUp)
    }

    override fun showGoYoutubeView() {
        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        goToYoutubeView.visibility = View.VISIBLE
        goToYoutubeView.startAnimation(slideUp)
        goToYoutubeView.setOnClickListener {
            goToYoutubeView.visibility = View.GONE
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutActivity.getIntroVideoUrl(context!!))))
        }
    }

    override fun showHistory(historyList: List<History>?) {
        if (historyList != null) {
            tutorialLayout.visibility = View.GONE
            historyRecyclerView.visibility = View.VISIBLE
            historyAdapter.setNewData(historyList)
        } else {
            tutorialLayout.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.GONE
        }
    }

    override fun updateHistory(index: Int, history: History) {
        historyAdapter.setData(index, history)
    }


    override fun getDecorView() = activity!!.window.decorView!!
    override fun setBigbangTipTv(strId: Int) = bigbangTipTv.setText(strId)
    override fun catchError(throwable: Throwable) = activity!!.timberAndToast(throwable)
    override fun getClipTvText() = clipTv.text.toString()


    companion object {
        const val REQUEST_CODE_OVERLAY_PERMISSION = 123
    }
}