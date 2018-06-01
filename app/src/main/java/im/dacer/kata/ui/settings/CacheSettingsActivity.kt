package im.dacer.kata.ui.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.baoyz.treasure.Treasure
import im.dacer.kata.Config
import im.dacer.kata.R
import im.dacer.kata.util.extension.setMyActionBar
import kotlinx.android.synthetic.main.activity_cache_settings.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

class CacheSettingsActivity : AppCompatActivity() {

    private val mConfig by lazy { Treasure.get(this, Config::class.java) }
//    private val appPref by lazy { MultiprocessPref(this) }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cache_settings)
        setMyActionBar(myToolbar)
        cacheSeekBar.progress = mConfig.cacheMax
        updateUI()

        cacheSeekBar.setOnProgressChangeListener(object : StyleActivity.SimpleListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                mConfig.cacheMax = value
                updateUI()
            }
        })
    }

    private fun updateUI() {
        cacheMaxNumTv.text = cacheSeekBar.progress.toString()
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
