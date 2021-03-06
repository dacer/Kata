package im.dacer.kata.ui.main

import android.content.Context
import android.os.Build
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.injection.ConfigPersistent
import im.dacer.kata.injection.qualifier.ApplicationContext
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.base.BasePresenter
import im.dacer.kata.util.helper.DialogHelper
import javax.inject.Inject

/**
 * Created by Dacer on 13/02/2018.
 */

@ConfigPersistent
class MainPresenter @Inject constructor(@ApplicationContext val context: Context) : BasePresenter<MainMvp>() {
    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var appPref: MultiprocessPref

    override fun attachView(mvpView: MainMvp) {
        super.attachView(mvpView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !settingUtility.androidQAlertShowed) {
            DialogHelper.showAndroidQAlert(mvpView.getActivity())
            settingUtility.androidQAlertShowed = true
        }
    }

    fun onResume() {
        ListenClipboardService.restartIfNeed(context, settingUtility.isListenClipboard)
    }

    fun getDrawerItems(): Array<IDrawerItem<*, *>> {
        val itemInbox = SecondaryDrawerItem().withIdentifier(DrawerItem.INBOX.id).withName(R.string.inbox)
        val itemWordBook = SecondaryDrawerItem().withIdentifier(DrawerItem.WORD_BOOK.id).withName(R.string.word_book)
        val itemLyric = SecondaryDrawerItem().withIdentifier(DrawerItem.LYRIC.id).withName(R.string.lyric).withSelectable(false)
        val itemNhkEasy = SecondaryDrawerItem().withIdentifier(DrawerItem.NHK_EASY.id).withName(R.string.nhk_news_easy)
        val itemNhk = SecondaryDrawerItem().withIdentifier(DrawerItem.NHK.id).withName(R.string.nhk_news)
        val itemSettings = SecondaryDrawerItem().withIdentifier(DrawerItem.SETTINGS.id).withName(R.string.settings).withSelectable(false)
        val itemAbout = SecondaryDrawerItem().withIdentifier(DrawerItem.ABOUT.id).withName(R.string.about).withSelectable(false)
        val result = arrayListOf<IDrawerItem<*, *>>(
                itemNhkEasy,
                itemNhk,
                DividerDrawerItem(),
                itemInbox,
                itemWordBook,
                itemSettings,
                itemAbout,
                DividerDrawerItem())

        if (appPref.easterEgg) result.add(5, itemLyric)
        return result.toTypedArray()
    }

    enum class DrawerItem(val id: Long) {
        INBOX(0), LYRIC(1), NHK_EASY(2), NHK(3), SETTINGS(4), ABOUT(5), WORD_BOOK(6);

        companion object {
            fun get(id: Long) : DrawerItem {
                for (item in DrawerItem.values()) {
                    if (item.id == id) return item
                }
                return DrawerItem.INBOX
            }
        }
    }
}