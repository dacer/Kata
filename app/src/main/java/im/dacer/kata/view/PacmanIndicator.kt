package im.dacer.kata.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.dinuscxj.refresh.IRefreshStatus

/**
 * https://github.com/81813780/AVLoadingIndicatorView/blob/master/library/src/main/java/com/wang/avi/Indicator.java
 */
class PacmanIndicator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IRefreshStatus {

    private val mUpdateListeners = HashMap<ValueAnimator,ValueAnimator.AnimatorUpdateListener>()
    private var mAnimators: ArrayList<ValueAnimator>? = null

    private val mPaint = Paint()
    private var mHasAnimators: Boolean = false
    private var translateX: Float = 0.toFloat()
    private var alpha: Int = 0
    private var degrees1: Float = 0.toFloat()
    private var degrees2: Float = 0.toFloat()


    init {
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        draw(canvas, mPaint)
    }

    override fun pullToRefresh() {}

    override fun refreshComplete() {}

    override fun releaseToRefresh() {}

    override fun reset() {
        stop()
    }

    override fun pullProgress(pullDistance: Float, pullProgress: Float) {
        startAnimatorByStep(pullProgress / 2)
    }

    override fun refreshing() {
        start()
    }

    fun start() {
        ensureAnimators()

        if (mAnimators == null) {
            return
        }

        if (isStarted()) {
            return
        }
        startAnimators()
        postInvalidate()
    }

    private fun startAnimatorByStep(fraction: Float) {
        ensureAnimators()

        if (mAnimators == null) {
            return
        }
        mAnimators?.run {
            for (i in 0 until this.size) {
                val animator = this[i]
                //when the animator restart , add the updateListener again because they
                // was removed by animator stop .
                val updateListener = mUpdateListeners[animator]

                animator.currentPlayTime = (fraction * animator.duration).toLong()
                updateListener?.onAnimationUpdate(animator)
            }
        }
    }
    private fun stop() {
        stopAnimators()
    }

    private fun draw(canvas: Canvas, paint: Paint) {
        drawPacman(canvas, paint)
        drawCircle(canvas, paint)
    }

    private fun isStarted(): Boolean {
        mAnimators?.run {
            for (animator in this) {
                return animator.isStarted
            }
        }
        return false
    }

    private fun startAnimators() {
        mAnimators?.run {
            for (i in 0 until this.size) {
                val animator = this[i]
                //when the animator restart , add the updateListener again because they
                // was removed by animator stop .
                val updateListener = mUpdateListeners[animator]
                if (updateListener != null) {
                    animator.addUpdateListener(updateListener)
                }

                animator.start()
            }
        }
    }


    private fun stopAnimators() {
        mAnimators?.run {
            for (animator in this) {
                if (animator.isStarted) {
                    animator.removeAllUpdateListeners()
                    animator.end()
                }
            }
        }
    }

    private fun ensureAnimators() {
        if (!mHasAnimators) {
            mAnimators = onCreateAnimators()
            mHasAnimators = true
        }
    }

    private fun getPacmanWidth(): Int {
        return height
    }

    private fun drawPacman(canvas: Canvas, paint: Paint) {
        val x = (getPacmanWidth() / 2).toFloat()
        val y = (height / 2).toFloat()

        canvas.save()

        canvas.translate(width / 2f, y)
        canvas.rotate(degrees1)
        paint.alpha = 255
        val rectF1 = RectF(-x / 1.7f, -y / 1.7f, x / 1.7f, y / 1.7f)
        canvas.drawArc(rectF1, 0f, 270f, true, paint)

        canvas.restore()

        canvas.save()
        canvas.translate(width / 2f, y)
        canvas.rotate(degrees2)
        paint.alpha = 255
        val rectF2 = RectF(-x / 1.7f,  -y / 1.7f, x / 1.7f, y / 1.7f)
        canvas.drawArc(rectF2, 90f, 270f, true, paint)
        canvas.restore()


    }

    private fun drawCircle(canvas: Canvas, paint: Paint) {
        val radius = (getPacmanWidth() / 11).toFloat()
        paint.alpha = alpha
        canvas.drawCircle(translateX, height / 2f, radius, paint)
    }

    companion object {
        private const val ANIM_DURATION = 600L
    }
    private fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = arrayListOf<ValueAnimator>()
        val startT = (getPacmanWidth() / 11).toFloat()
        val translationAnim = ValueAnimator.ofFloat(width - startT, width / 2f)
        translationAnim.duration = ANIM_DURATION
        translationAnim.interpolator = LinearInterpolator()
        translationAnim.repeatCount = -1
        addUpdateListener(translationAnim, ValueAnimator.AnimatorUpdateListener{ animation ->
            translateX = animation.animatedValue as Float
            postInvalidate()
        })

        val alphaAnim = ValueAnimator.ofInt(255, 122)
        alphaAnim.duration = ANIM_DURATION
        alphaAnim.repeatCount = -1
        addUpdateListener(alphaAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            alpha = animation.animatedValue as Int
            postInvalidate()
        })

        val rotateAnim1 = ValueAnimator.ofFloat(0f, 45f, 0f)
        rotateAnim1.duration = ANIM_DURATION
        rotateAnim1.repeatCount = -1
        addUpdateListener(rotateAnim1, ValueAnimator.AnimatorUpdateListener { animation ->
            degrees1 = animation.animatedValue as Float
            postInvalidate()
        })

        val rotateAnim2 = ValueAnimator.ofFloat(0f, -45f, 0f)
        rotateAnim2.duration = ANIM_DURATION
        rotateAnim2.repeatCount = -1
        addUpdateListener(rotateAnim2, ValueAnimator.AnimatorUpdateListener { animation ->
            degrees2 = animation.animatedValue as Float
            postInvalidate()
        })

        animators.add(translationAnim)
        animators.add(alphaAnim)
        animators.add(rotateAnim1)
        animators.add(rotateAnim2)
        return animators
    }


    private fun addUpdateListener(animator: ValueAnimator, updateListener: ValueAnimator.AnimatorUpdateListener) {
        mUpdateListeners[animator] = updateListener
    }
}