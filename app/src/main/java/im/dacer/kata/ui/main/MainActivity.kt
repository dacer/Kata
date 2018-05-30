package im.dacer.kata.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback
import im.dacer.kata.R
import im.dacer.kata.adapter.HistoryAdapter
import im.dacer.kata.core.extension.timberAndToast
import im.dacer.kata.core.extension.toast
import im.dacer.kata.core.model.History
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.AboutActivity
import im.dacer.kata.ui.inbox.InboxFragment
import im.dacer.kata.ui.inbox.InboxFragment.Companion.REQUEST_CODE_OVERLAY_PERMISSION
import im.dacer.kata.ui.lyric.LyricActivity
import im.dacer.kata.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), MainMvp {
    private val mainPresenter by lazy { MainPresenter(baseContext, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val newFragment = InboxFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.frameLayout, newFragment).commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
//            mainPresenter.restartListenService()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.lyric -> {
                startActivity(Intent(this, LyricActivity::class.java))
            }
            R.id.about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
