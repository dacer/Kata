package im.dacer.kata.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import im.dacer.kata.R
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.ui.about.AboutActivity
import im.dacer.kata.ui.base.BaseActivity
import im.dacer.kata.ui.lyric.LyricActivity
import im.dacer.kata.ui.main.MainPresenter.DrawerItem
import im.dacer.kata.ui.main.inbox.InboxFragment
import im.dacer.kata.ui.main.inbox.InboxFragment.Companion.REQUEST_CODE_OVERLAY_PERMISSION
import im.dacer.kata.ui.main.news.NewsFragment
import im.dacer.kata.ui.main.wordbook.WordBookFragment
import im.dacer.kata.ui.settings.SettingsActivity
import im.dacer.kata.util.extension.startActivity
import im.dacer.kata.util.helper.AnkiDroidHelper
import kotlinx.android.synthetic.main.activity_main.*
import qiu.niorgai.StatusBarCompat
import javax.inject.Inject


class MainActivity : BaseActivity(), MainMvp {

    @Inject lateinit var mainPresenter: MainPresenter
    @Inject lateinit var settingUtility: SettingUtility
    private val drawer by lazy { initDrawer() }

    override fun layoutId() = R.layout.activity_main
    override fun getActivity() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        mainPresenter.attachView(this)
        setSupportActionBar(myToolbar as Toolbar)

        drawer.setSelection(DrawerItem.get(settingUtility.lastExitTab).id, true)
    }

    private fun initDrawer(): Drawer {
        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(ColorDrawable(ContextCompat.getColor(this, R.color.material_drawer_dark_background)))
                .build()
        val drawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(myToolbar as Toolbar)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(true)
                .addDrawerItems(*mainPresenter.getDrawerItems())
                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    when (drawerItem.identifier) {
                        DrawerItem.INBOX.id -> switchFragment(InboxFragment(), R.string.inbox)
                        DrawerItem.LYRIC.id -> startActivity(LyricActivity::class.java)
                        DrawerItem.NHK_EASY.id -> switchFragment(NewsFragment.newInstance(NewsFragment.NewsType.NHK_EASY), R.string.nhk_news_easy)
                        DrawerItem.NHK.id -> switchFragment(NewsFragment.newInstance(NewsFragment.NewsType.NHK), R.string.nhk_news)
                        DrawerItem.SETTINGS.id -> startActivity(SettingsActivity::class.java)
                        DrawerItem.ABOUT.id -> startActivity(AboutActivity::class.java)
                        DrawerItem.WORD_BOOK.id -> switchFragment(WordBookFragment(), R.string.word_book)
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


    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        if (requestCode == AnkiDroidHelper.ANKI_PERMISSION_REQUEST &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val wordFragment = supportFragmentManager.findFragmentById(R.id.frameLayout) as WordBookFragment
            wordFragment.exportAnki()
        }
    }

    private fun switchFragment(fragment: Fragment, titleRes: Int = R.string.app_name) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout, fragment).commit()
        supportActionBar?.setTitle(titleRes)
        supportActionBar?.subtitle = null
    }
}
