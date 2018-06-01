package im.dacer.kata.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import im.dacer.kata.R
import im.dacer.kata.util.helper.SchemeHelper
import im.dacer.kata.util.LogUtils

class FloatingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val mWindowManager: WindowManager
    private val mMargin: Int
    private val mMarginY: Int
    private val mDismissTask = Runnable { dismiss() }
    private var isShow: Boolean = false
    var mText: String? = null


    init {
        setImageResource(R.drawable.floating_button)
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

            val layoutParams = WindowManager.LayoutParams(w, h, WINDOW_TYPE, WINDOW_FLAG, PixelFormat.TRANSLUCENT)
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
            animate().scaleY(1f).scaleX(1f).setDuration(ANIMATION_DURATION.toLong()).setListener(null).start()
        }

        removeCallbacks(mDismissTask)
        postDelayed(mDismissTask, 3000)
    }

    private fun dismiss() {
        if (isShow) {
            animate().cancel()
            animate().scaleX(0f).scaleY(0f).setDuration(ANIMATION_DURATION.toLong()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    try {
                        mWindowManager.removeView(this@FloatingView)
                    } catch (e: Exception) {
                        LogUtils.log(e)
                    }

                    isShow = false
                }
            }).start()
        }
        removeCallbacks(mDismissTask)
    }

    companion object {
        const val WINDOW_FLAG = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        var WINDOW_TYPE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            WindowManager.LayoutParams.TYPE_TOAST
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        private const val ANIMATION_DURATION = 500
    }
}
