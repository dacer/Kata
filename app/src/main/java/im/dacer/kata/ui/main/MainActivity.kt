package im.dacer.kata.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import com.mikepenz.materialdrawer.DrawerBuilder
import im.dacer.kata.R
import im.dacer.kata.ui.AboutActivity
import im.dacer.kata.ui.inbox.InboxFragment
import im.dacer.kata.ui.inbox.InboxFragment.Companion.REQUEST_CODE_OVERLAY_PERMISSION
import im.dacer.kata.ui.lyric.LyricActivity
import im.dacer.kata.ui.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import im.dacer.kata.ui.base.BaseActivity
import com.mikepenz.materialdrawer.AccountHeaderBuilder


class MainActivity : BaseActivity(), MainMvp {
    private val mainPresenter by lazy { MainPresenter(baseContext, this) }
    private val drawer by lazy { initDrawer() }

    enum class DrawerItem(val id: Long) {
        INBOX(0), LYRIC(1), NHK_EASY(2), NHK(3), SETTINGS(4), ABOUT(5)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(my_toolbar)

        if (savedInstanceState == null) {
            val newFragment = InboxFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.frameLayout, newFragment).commit()
        }
        drawer.setSelection(DrawerItem.INBOX.id)
    }

    private fun initDrawer() : Drawer {
        val itemInbox = PrimaryDrawerItem().withIdentifier(DrawerItem.INBOX.id).withName(R.string.inbox)
        val itemLyric = PrimaryDrawerItem().withIdentifier(DrawerItem.LYRIC.id).withName(R.string.lyric)
        val itemNhkEasy = SecondaryDrawerItem().withIdentifier(DrawerItem.NHK_EASY.id).withName(R.string.nhk_easy)
        val itemNhk = SecondaryDrawerItem().withIdentifier(DrawerItem.NHK.id).withName(R.string.nhk)
        val itemSettings = SecondaryDrawerItem().withIdentifier(DrawerItem.SETTINGS.id).withName(R.string.settings)
        val itemAbout = SecondaryDrawerItem().withIdentifier(DrawerItem.ABOUT.id).withName(R.string.about)
        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(ColorDrawable(Color.BLACK))
                .build()
        return DrawerBuilder()
                .withActivity(this)
                .withToolbar(my_toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemInbox,
                        DividerDrawerItem(),
                        itemLyric,
                        DividerDrawerItem(),
                        itemNhkEasy,
                        DividerDrawerItem(),
                        itemNhk,
                        DividerDrawerItem(),
                        itemSettings,
                        DividerDrawerItem(),
                        itemAbout
                )
                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    when(drawerItem.identifier) {
                        DrawerItem.INBOX.id -> {}
                        DrawerItem.LYRIC.id -> {}
                        DrawerItem.NHK_EASY.id -> {}
                        DrawerItem.NHK.id -> {}
                        DrawerItem.SETTINGS.id -> {}
                        DrawerItem.ABOUT.id -> {}
                    }
                    return@withOnDrawerItemClickListener false
                }
                .withGenerateMiniDrawer(true)
                .build()
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
