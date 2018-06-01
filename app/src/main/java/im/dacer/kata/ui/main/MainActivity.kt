package im.dacer.kata.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import com.mikepenz.materialdrawer.DrawerBuilder
import im.dacer.kata.R
import im.dacer.kata.ui.AboutActivity
import im.dacer.kata.ui.main.inbox.InboxFragment
import im.dacer.kata.ui.main.inbox.InboxFragment.Companion.REQUEST_CODE_OVERLAY_PERMISSION
import im.dacer.kata.ui.lyric.LyricActivity
import im.dacer.kata.ui.settings.SettingsActivity
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import im.dacer.kata.ui.base.BaseActivity
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import im.dacer.kata.util.extension.startActivity
import im.dacer.kata.ui.main.news.NewsFragment
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), MainMvp {

    @Inject lateinit var mainPresenter: MainPresenter
    private val drawer by lazy { initDrawer() }

    enum class DrawerItem(val id: Long) {
        INBOX(0), LYRIC(1), NHK_EASY(2), NHK(3), SETTINGS(4), ABOUT(5)
    }
    override fun layoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mainPresenter.attachView(this)
        setSupportActionBar(myToolbar as Toolbar)

        if (savedInstanceState == null) {
//            val ft = supportFragmentManager.beginTransaction()
//            ft.add(R.id.frameLayout, InboxFragment()).commit()
            drawer.setSelection(DrawerItem.INBOX.id, true)
        }
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
                .withToolbar(myToolbar as Toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemInbox,
                        DividerDrawerItem(),
                        itemNhkEasy,
                        DividerDrawerItem(),
                        itemNhk,
                        DividerDrawerItem(),
                        itemLyric,
                        DividerDrawerItem(),
                        itemSettings,
                        DividerDrawerItem(),
                        itemAbout
                )
                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    when(drawerItem.identifier) {
                        DrawerItem.INBOX.id -> switchFragment(InboxFragment(), R.string.inbox)
                        DrawerItem.LYRIC.id -> startActivity(LyricActivity::class.java)
                        DrawerItem.NHK_EASY.id -> switchFragment(NewsFragment(), R.string.nhk_easy)
                        DrawerItem.NHK.id -> {}
                        DrawerItem.SETTINGS.id -> startActivity(SettingsActivity::class.java)
                        DrawerItem.ABOUT.id -> startActivity(AboutActivity::class.java)
                    }
                    return@withOnDrawerItemClickListener false

                }
                .build()
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPresenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
//            mainPresenter.restartListenService()
        }
    }

    private fun switchFragment(fragment: Fragment, titleRes: Int = R.string.app_name) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout, fragment).commit()
        supportActionBar?.setTitle(titleRes)
    }
}
