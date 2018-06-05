package im.dacer.kata.ui.base

import android.os.Bundle
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper

abstract class BaseTransparentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentNav()
    }

}