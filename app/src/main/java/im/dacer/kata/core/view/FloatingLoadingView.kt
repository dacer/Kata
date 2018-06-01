package im.dacer.kata.core.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.widget.RelativeLayout
import im.dacer.kata.R
import im.dacer.kata.core.util.SchemeHelper
import im.dacer.kata.segment.util.LogUtils
import timber.log.Timber

class FloatingLoadingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private val mWindowManager: WindowManager
    private val mMargin: Int
    private val mMarginY: Int
    private var isShow: Boolean = false
    var mText: String? = null


    init {
        inflate(getContext(), R.layout.floating_loading_view, this)
        mMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
        mMarginY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180f, resources.displayMetrics).toInt()
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        setOnClickListener {
            mText?.run { SchemeHelper.startKata(getContext(), this, 0) }
            dismiss()
        }
    }

    fun show() {
        if (!isShow) {
            val w = WindowManager.LayoutParams.WRAP_CONTENT
            val h = WindowManager.LayoutParams.WRAP_CONTENT

            val layoutParams = WindowManager.LayoutParams(w, h, FloatingView.WINDOW_TYPE, FloatingView.WINDOW_FLAG, PixelFormat.TRANSLUCENT)
            layoutParams.gravity = Gravity.RIGHT or Gravity.BOTTOM
            layoutParams.x = mMargin
            layoutParams.y = mMarginY

            try {
                mWindowManager.addView(this, layoutParams)
            } catch (e: WindowManager.BadTokenException) {
                LogUtils.log(e)
            }

            isShow = true

            scaleX = 0f
            scaleY = 0f
            animate().cancel()
            animate().scaleY(1f)
                    .scaleX(1f)
                    .setDuration(ANIMATION_DURATION.toLong())
                    .setListener(null)
                    .start()
        }
    }

    fun dismiss() {
        if (isShow) {
            isShow = false
            animate().cancel()
            animate().scaleX(0f)
                    .scaleY(0f)
                    .setDuration(ANIMATION_DURATION.toLong())
                    .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    try {
                        mWindowManager.removeView(this@FloatingLoadingView)
                    } catch (e: Exception) {
                        LogUtils.log(e)
                    }
                }
            }).start()
        }
    }

    companion object {

        private const val ANIMATION_DURATION = 500
    }
}
