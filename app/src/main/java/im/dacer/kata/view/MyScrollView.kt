package im.dacer.kata.view

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet

class MyScrollView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    var overScrollListener: OverScrollListener? = null

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (clampedY) overScrollListener?.onOverScroll(scrollY == 0)
    }

    interface OverScrollListener {
        fun onOverScroll(topOverScroll: Boolean)
    }
}