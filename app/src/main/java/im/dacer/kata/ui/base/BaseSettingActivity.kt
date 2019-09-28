package im.dacer.kata.ui.base

import android.support.v7.widget.SwitchCompat
import android.view.MenuItem
import android.view.View

abstract class BaseSettingActivity : BaseSwipeActivity() {

    protected fun Array<View>.setSwitchListener(listener: (checked: Boolean) -> Unit = {}) {
        var switchCompat: SwitchCompat? = null
        for (v in this) {
            if (v is SwitchCompat) {
                switchCompat = v
                break
            }
        }

        this.forEach {
            if (it is SwitchCompat) {
                it.setOnCheckedChangeListener { _, isChecked ->
                    listener(isChecked)
                    updateUI()
                }
            } else {
                it.setOnClickListener { switchCompat!!.toggle() }
            }
        }
    }

    abstract fun updateUI()

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