package im.dacer.kata.util

import android.support.design.widget.Snackbar
import android.view.View
import im.dacer.kata.R

class SnackBarHelper {
    companion object {
        fun showRedo(decorView: View, string: String,
                     timeoutDismissListener: (Unit) -> (Unit), redoListener: (Unit) -> (Unit)) {
            val snackBar = Snackbar.make(decorView, string, Snackbar.LENGTH_LONG)
                    .setAction(R.string.redo) { redoListener(Unit) }
//            val navBarHeight = UIUtils.getNavigationBarHeight(context)
//            if (navBarHeight > 0) {
//                val snackBarView = snackBar.view as FrameLayout
//                val params = snackBarView.getChildAt(0).layoutParams as FrameLayout.LayoutParams
//                params.setMargins(params.leftMargin,
//                        params.topMargin,
//                        params.rightMargin,
//                        params.bottomMargin + navBarHeight)
//
//                snackBarView.getChildAt(0).layoutParams = params
//            }
            snackBar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_CONSECUTIVE) {
                        timeoutDismissListener(Unit)
                    }
                }
            })
            snackBar.show()
        }
    }
}