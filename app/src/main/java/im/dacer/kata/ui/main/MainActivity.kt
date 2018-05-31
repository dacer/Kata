package im.dacer.kata.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import im.dacer.kata.ui.base.BaseActivity
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import im.dacer.kata.core.extension.startActivity


class MainActivity : BaseActivity(), MainMvp {
    private val mainPresenter by lazy { MainPresenter(baseContext, this) }
    private val drawer by lazy { initDrawer() }

    enum class DrawerItem(val id: Long) {
        INBOX(0), LYRIC(1), NHK_EASY(2), NHK(3), SETTINGS(4), ABOUT(5)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(myToolbar)

        if (savedInstanceState == null) {
            val newFragment = InboxFragment()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.frameLayout, newFragment).commit()
        }
        drawer.setSelection(DrawerItem.INBOX.id)
    }

    private fun initDrawer() : Drawer {
        val itemInbox = SecondaryDrawerItem().withIdentifier(DrawerItem.INBOX.id).withName(R.string.inbox)
        val itemLyric = SecondaryDrawerItem().withIdentifier(DrawerItem.LYRIC.id).withName(R.string.lyric).withSelectable(false)
        val itemNhkEasy = SecondaryDrawerItem().withIdentifier(DrawerItem.NHK_EASY.id).withName(R.string.nhk_easy)
        val itemNhk = SecondaryDrawerItem().withIdentifier(DrawerItem.NHK.id).withName(R.string.nhk)
        val itemSettings = SecondaryDrawerItem().withIdentifier(DrawerItem.SETTINGS.id).withName(R.string.settings).withSelectable(false)
        val itemAbout = SecondaryDrawerItem().withIdentifier(DrawerItem.ABOUT.id).withName(R.string.about).withSelectable(false)
        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(ColorDrawable(Color.BLACK))
                .build()
        return DrawerBuilder()
                .withActivity(this)
                .withToolbar(myToolbar)
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
                        DrawerItem.LYRIC.id -> { startActivity(LyricActivity::class.java) }
                        DrawerItem.NHK_EASY.id -> {}
                        DrawerItem.NHK.id -> {}
                        DrawerItem.SETTINGS.id -> { startActivity(SettingsActivity::class.java) }
                        DrawerItem.ABOUT.id -> { startActivity(AboutActivity::class.java) }
                    }
                    return@withOnDrawerItemClickListener false
                }
                .build()
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
//            mainPresenter.restartListenService()
        }
    }


}
