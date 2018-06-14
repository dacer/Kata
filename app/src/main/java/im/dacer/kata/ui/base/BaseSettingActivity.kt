package im.dacer.kata.ui.base

import android.support.v7.widget.SwitchCompat
import android.view.View

abstract class BaseSettingActivity : BaseTransparentSwipeActivity() {

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
                it.setOnClickListener { _ -> switchCompat!!.toggle() }
            }
        }
    }

    abstract fun updateUI()
}