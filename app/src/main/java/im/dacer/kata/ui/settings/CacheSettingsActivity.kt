package im.dacer.kata.ui.settings

import android.os.Bundle
import android.view.MenuItem
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.ui.base.BaseSettingActivity
import im.dacer.kata.util.extension.setMyActionBar
import kotlinx.android.synthetic.main.activity_cache_settings.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import javax.inject.Inject

class CacheSettingsActivity : BaseSettingActivity() {
    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var multiprocessPref: MultiprocessPref

    override fun layoutId() = R.layout.activity_cache_settings

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        setMyActionBar(myToolbar)
        cacheSeekBar.progress = settingUtility.cacheMax
        updateUI()

        cacheSeekBar.setOnProgressChangeListener(object : StyleActivity.SimpleListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                settingUtility.cacheMax = value
                updateUI()
            }
        })

        arrayOf(newsCachingWifiOnlyLayout, newsCachingWifiOnly).setSwitchListener {
            multiprocessPref.newsCachingWifiOnly = it
        }

        arrayOf(showPicWifiOnlyLayout, showPicWifiOnly).setSwitchListener {
            multiprocessPref.showPicWifiOnly = it
        }
    }

    override fun updateUI() {
        cacheMaxNumTv.text = cacheSeekBar.progress.toString()
        newsCachingWifiOnly.isChecked = multiprocessPref.newsCachingWifiOnly
    }
}
