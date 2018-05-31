package im.dacer.kata.ui.main

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
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.widget.PopupView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Dacer on 13/02/2018.
 */
class MainPresenter(val context: Context, private val mainMvp: MainMvp) {
    private val treasure by lazy { Treasure.get(context, Config::class.java) }

    fun onResume() {
        restartListenService()
    }

    fun restartListenService() {
        if (treasure.isListenClipboard) {
            ListenClipboardService.restart(context)
        }
    }
}