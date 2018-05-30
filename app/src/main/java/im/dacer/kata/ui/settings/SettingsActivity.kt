package im.dacer.kata.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.baoyz.treasure.Treasure
import im.dacer.kata.Config
import im.dacer.kata.R
import im.dacer.kata.SearchEngine
import im.dacer.kata.core.data.MultiprocessPref
import im.dacer.kata.core.extension.setMyActionBar
import im.dacer.kata.core.util.LangUtils
import im.dacer.kata.core.util.WebParser
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.AboutActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val mConfig by lazy { Treasure.get(this, Config::class.java) }
    private val appPref by lazy { MultiprocessPref(this) }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
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
            mConfig.isListenClipboard = isChecked
            refreshService()
            updateUI()
        }
        cacheLayout.setOnClickListener { startActivity(Intent(this@SettingsActivity, CacheSettingsActivity::class.java)) }
        bigbangStyle.setOnClickListener { startActivity(Intent(this@SettingsActivity, StyleActivity::class.java)) }
        updateUI()

    }

    private fun refreshService() {
        if (mConfig.isListenClipboard) {
            ListenClipboardService.restart(applicationContext)
        } else {
            ListenClipboardService.stop(applicationContext)
        }
    }

    private fun updateUI() {
        searchEngineTv.text = appPref.searchEngine
        showFloatDialogSwit.isChecked = appPref.showFloatDialog
        listenClipboardSwitch.isChecked = mConfig.isListenClipboard
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
