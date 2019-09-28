package im.dacer.kata.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.about.AboutActivity
import im.dacer.kata.ui.base.BaseSettingActivity
import im.dacer.kata.util.extension.setMyActionBar
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : BaseSettingActivity() {
    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var appPref: MultiprocessPref

    override fun layoutId() = R.layout.activity_settings

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        setMyActionBar(myToolbar)

        arrayOf(listenClipboardLayout, listenClipboardSwitch).setSwitchListener {
            settingUtility.isListenClipboard = it
            updateUI()
            refreshService()
        }
        tutorialVideoLayout.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutActivity.getIntroVideoUrl(this))))
        }

        textAnalysis.setOnClickListener { startActivity(Intent(this@SettingsActivity, TextAnalysisSettingsActivity::class.java)) }
        cacheLayout.setOnClickListener { startActivity(Intent(this@SettingsActivity, CacheSettingsActivity::class.java)) }
        bigbangStyle.setOnClickListener { startActivity(Intent(this@SettingsActivity, StyleActivity::class.java)) }
        updateUI()

    }

    private fun refreshService() {
        if (settingUtility.isListenClipboard) {
            ListenClipboardService.restart(applicationContext)
        } else {
            ListenClipboardService.stop(applicationContext)
        }
    }

    override fun updateUI() {
        listenClipboardSwitch.isChecked = settingUtility.isListenClipboard
    }
}
