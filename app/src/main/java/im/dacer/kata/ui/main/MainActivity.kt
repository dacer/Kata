package im.dacer.kata.ui.main

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import im.dacer.kata.R
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.ui.about.AboutActivity
import im.dacer.kata.ui.base.BaseTransparentActivity
import im.dacer.kata.ui.lyric.LyricActivity
import im.dacer.kata.ui.main.inbox.InboxFragment
import im.dacer.kata.ui.main.inbox.InboxFragment.Companion.REQUEST_CODE_OVERLAY_PERMISSION
import im.dacer.kata.ui.main.news.NewsFragment
import im.dacer.kata.ui.settings.SettingsActivity
import im.dacer.kata.util.extension.startActivity
import kotlinx.android.synthetic.main.activity_main.*
import qiu.niorgai.StatusBarCompat
import javax.inject.Inject


class MainActivity : BaseTransparentActivity(), MainMvp {

    @Inject lateinit var mainPresenter: MainPresenter
    @Inject lateinit var settingUtility: SettingUtility
    private val drawer by lazy { initDrawer() }

    enum class DrawerItem(val id: Long) {
        INBOX(0), LYRIC(1), NHK_EASY(2), NHK(3), SETTINGS(4), ABOUT(5);

        companion object {
            fun get(id: Long) : DrawerItem {
                for (item in DrawerItem.values()) {
                    if (item.id == id) return item
                }
                return DrawerItem.INBOX
            }
        }
    }
    override fun layoutId() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mainPresenter.attachView(this)
        setSupportActionBar(myToolbar as Toolbar)

        drawer.setSelection(DrawerItem.get(settingUtility.lastExitTab).id, true)

        if (!settingUtility.demoAlertHasShown) {
            MaterialDialog.Builder(this)
                    .title(R.string.demo_alert)
                    .content(R.string.demo_alert_msg)
                    .cancelable(false)
                    .negativeText(R.string.demo_alert_no)
                    .onNegative { _, _ -> finish() }
                    .positiveText(R.string.demo_alert_ok)
                    .onPositive { _, _ -> settingUtility.demoAlertHasShown = true }
                    .show()
        }
    }

    private fun initDrawer() : Drawer {
        val itemInbox = SecondaryDrawerItem().withIdentifier(DrawerItem.INBOX.id).withName(R.string.inbox)
        val itemLyric = SecondaryDrawerItem().withIdentifier(DrawerItem.LYRIC.id).withName(R.string.lyric).withSelectable(false)
        val itemNhkEasy = SecondaryDrawerItem().withIdentifier(DrawerItem.NHK_EASY.id).withName(R.string.nhk_news_easy)
        val itemNhk = SecondaryDrawerItem().withIdentifier(DrawerItem.NHK.id).withName(R.string.nhk_news)
        val itemSettings = SecondaryDrawerItem().withIdentifier(DrawerItem.SETTINGS.id).withName(R.string.settings).withSelectable(false)
        val itemAbout = SecondaryDrawerItem().withIdentifier(DrawerItem.ABOUT.id).withName(R.string.about).withSelectable(false)
        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(ColorDrawable(ContextCompat.getColor(this, R.color.material_drawer_dark_background)))
                .build()
        val drawer =  DrawerBuilder()
                .withActivity(this)
                .withToolbar(myToolbar as Toolbar)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
                        itemInbox,
                        itemNhkEasy,
                        itemNhk,
                        itemLyric,
                        itemSettings,
                        itemAbout,
                        DividerDrawerItem()
                )
                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    when(drawerItem.identifier) {
                        DrawerItem.INBOX.id -> switchFragment(InboxFragment(), R.string.inbox)
                        DrawerItem.LYRIC.id -> startActivity(LyricActivity::class.java)
                        DrawerItem.NHK_EASY.id -> switchFragment(NewsFragment.newInstance(NewsFragment.NewsType.NHK_EASY), R.string.nhk_news_easy)
                        DrawerItem.NHK.id -> switchFragment(NewsFragment.newInstance(NewsFragment.NewsType.NHK), R.string.nhk_news)
                        DrawerItem.SETTINGS.id -> startActivity(SettingsActivity::class.java)
                        DrawerItem.ABOUT.id -> startActivity(AboutActivity::class.java)
                    }
                    return@withOnDrawerItemClickListener false

                }
                .build()

        StatusBarCompat.translucentStatusBar(this, true)
        return drawer
    }

    override fun onResume() {
        super.onResume()
        mainPresenter.onResume()
    }

    override fun onDestroy() {
        mainPresenter.detachView()
        settingUtility.lastExitTab = drawer.currentSelection
        super.onDestroy()
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
        supportActionBar?.subtitle = null
    }
}
