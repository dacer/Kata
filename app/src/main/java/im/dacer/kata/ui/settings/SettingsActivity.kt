package im.dacer.kata.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.AboutActivity
import im.dacer.kata.ui.base.BaseTransparentSwipeActivity
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.WebParser
import im.dacer.kata.util.engine.SearchEngine
import im.dacer.kata.util.extension.setMyActionBar
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : BaseTransparentSwipeActivity() {
    @Inject lateinit var settingUtility: SettingUtility
    @Inject lateinit var appPref: MultiprocessPref

    override fun layoutId() = R.layout.activity_settings

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        setMyActionBar(myToolbar)

        searchEngine.setOnClickListener {
            MaterialDialog.Builder(this)
                    .items(SearchEngine.getSupportSearchEngineList().toList())
                    .itemsCallback { _, _, pos, _ ->
                        appPref.searchEngine = SearchEngine.getSupportSearchEngineList()[pos]
                        updateUI()
                    }
                    .show()
        }

        enhancedModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            appPref.enhancedMode = isChecked
            refreshService()
            updateUI()
        }

        translationTarget.setOnClickListener {
            MaterialDialog.Builder(this)
                    .items(LangUtils.LANG_LIST.toList())
                    .itemsCallback { _, _, pos, _ ->
                        appPref.targetLang = LangUtils.LANG_KEY_LIST[pos]
                        updateUI()
                    }
                    .show()
        }

        webPageParser.setOnClickListener {
            MaterialDialog.Builder(this)
                    .items(WebParser.getParserNameArray(this).toList())
                    .itemsCallback { _, _, pos, _ ->
                        appPref.webParser = WebParser.Parser.values()[pos]
                        updateUI()
                    }
                    .show()
        }

        tutorialVideoLayout.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutActivity.YOUTUBE_LINK)))
        }

        showFloatDialogSwit.setOnCheckedChangeListener { _, isChecked ->
            appPref.showFloatDialog = isChecked
            updateUI()
        }

        listenClipboardSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingUtility.isListenClipboard = isChecked
            refreshService()
            updateUI()
        }
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

    private fun updateUI() {
        searchEngineTv.text = appPref.searchEngine
        showFloatDialogSwit.isChecked = appPref.showFloatDialog
        listenClipboardSwitch.isChecked = settingUtility.isListenClipboard
        webPageParserTv.setText(appPref.webParser.stringRes)
        enhancedModeSwitch.isChecked = appPref.enhancedMode
        translationTargetTv.text = LangUtils.getLangByKey(appPref.targetLang)

        enhancedModeSwitch.isEnabled = listenClipboardSwitch.isChecked
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
