package im.dacer.kata.ui.main.inbox

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.view.animation.AnimationUtils
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import im.dacer.kata.R
import im.dacer.kata.adapter.HistoryAdapter
import im.dacer.kata.core.extension.timberAndToast
import im.dacer.kata.core.model.History
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.AboutActivity
import im.dacer.kata.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_inbox.*
import org.jetbrains.anko.support.v4.toast

class InboxFragment: BaseFragment(), InboxMvp {
    private val mainPresenter by lazy { InboxPresenter(context!!, this) }
    private val historyAdapter = HistoryAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_inbox, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popupView.listener = mainPresenter
        mainPresenter.importDictDb()
        historyRecyclerView.layoutManager = LinearLayoutManager(context)

        val itemDragAndSwipeCallback = ItemDragAndSwipeCallback(historyAdapter)
        val itemTouchHelper = ItemTouchHelper(itemDragAndSwipeCallback)
        historyAdapter.openLoadAnimation()
        historyAdapter.bindToRecyclerView(historyRecyclerView)
        itemTouchHelper.attachToRecyclerView(historyRecyclerView)
        historyAdapter.setOnItemClickListener { _, _, pos -> mainPresenter.onHistoryClicked(pos)}
        historyAdapter.setOnItemLongClickListener { _, _, pos -> mainPresenter.onHistoryLongClicked(activity!!, pos) }
        historyAdapter.setEmptyView(R.layout.empty_history)
        val bottomView = layoutInflater.inflate(R.layout.item_history_bottom, historyRecyclerView.parent as ViewGroup, false)
        historyAdapter.setFooterView(bottomView)
        historyAdapter.enableSwipeItem()
        historyAdapter.setOnItemSwipeListener(mainPresenter.swipeListener)

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
        mainPresenter.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val canDraw = Settings.canDrawOverlays(activity)
            permissionErrorLayout.visibility = if (canDraw) View.GONE else View.VISIBLE
        }
        nothingHappenedView.visibility = View.GONE
        goToYoutubeView.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        mainPresenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPresenter.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
//            mainPresenter.restartListenService()
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
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutActivity.YOUTUBE_LINK)))
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