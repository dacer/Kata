package im.dacer.kata.ui.base

import android.os.Bundle

abstract class BaseTransparentSwipeActivity : BaseSwipeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentNav()
    }

}