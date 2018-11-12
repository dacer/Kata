package im.dacer.kata.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.kata.BuildConfig
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.local.SettingUtility
import im.dacer.kata.service.ListenClipboardService
import im.dacer.kata.ui.about.AboutActivity
import im.dacer.kata.ui.base.BaseSettingActivity
import im.dacer.kata.util.LangUtils
import im.dacer.kata.util.engine.SearchEngine
import im.dacer.kata.util.extension.setMyActionBar
import im.dacer.kata.util.webparse.WebParser
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

        searchEngine.setOnClickListener {
            MaterialDialog.Builder(this)
                    .items(SearchEngine.getSupportSearchEngineList().toList())
                    .itemsCallback { _, _, pos, _ ->
                        appPref.searchEngine = SearchEngine.getSupportSearchEngineList()[pos]
                        updateUI()
                    }
                    .show()
        }

        arrayOf(listenClipboardLayout, listenClipboardSwitch).setSwitchListener {
            settingUtility.isListenClipboard = it
            updateUI()
            refreshService()
        }
        arrayOf(analyzeUrlInClipboardLayout, analyzeUrlInClipboardSwitch).setSwitchListener {
            appPref.analyzeUrlInClipboard = it
            updateUI()
        }
        arrayOf(enhancedModeLayout, enhancedModeSwitch).setSwitchListener {
            if (it) showIgnoreBatteryOptimizationDialog()
            appPref.enhancedMode = it
            refreshService()
        }
        arrayOf(showFloatDialogLayout, showFloatDialogSwit).setSwitchListener {
            appPref.showFloatDialog = it
        }
        arrayOf(enableWordBookLayout, enableWordBookSwit).setSwitchListener {
            appPref.enableWordBook = it
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
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(AboutActivity.getIntroVideoUrl(this))))
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

    override fun updateUI() {
        searchEngineTv.text = appPref.searchEngine
        showFloatDialogSwit.isChecked = appPref.showFloatDialog
        listenClipboardSwitch.isChecked = settingUtility.isListenClipboard
        webPageParserTv.setText(appPref.webParser.stringRes)
        enhancedModeSwitch.isChecked = appPref.enhancedMode
        translationTargetTv.text = LangUtils.getLangByKey(appPref.targetLang)
        enableWordBookSwit.isChecked = appPref.enableWordBook
        analyzeUrlInClipboardSwitch.isChecked = appPref.analyzeUrlInClipboard

        if (settingUtility.isListenClipboard) {
            enhancedModeLayout.visibility = View.VISIBLE
            analyzeUrlInClipboardLayout.visibility = View.VISIBLE
        } else {
            enhancedModeLayout.visibility = View.GONE
            analyzeUrlInClipboardLayout.visibility = View.GONE
        }
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

    private fun showIgnoreBatteryOptimizationDialog() {
        if (BuildConfig.FLAVOR != "coolapk") {
            checkBatteryOptimization()
            return
        }
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    private fun checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (!isBatteryOptimized()) return
        val appName = resources.getString(R.string.app_name)
        Toast.makeText(applicationContext, getString(R.string.optimization_steps, appName), Toast.LENGTH_LONG).show()

        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        startActivity(intent)

    }

    private fun isBatteryOptimized(): Boolean {
        val powerService = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !powerService.isIgnoringBatteryOptimizations(applicationContext.packageName)
        }
        return false
    }

}
