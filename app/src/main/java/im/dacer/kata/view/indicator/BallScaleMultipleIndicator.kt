package im.dacer.kata.view.indicator

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator
import com.wang.avi.Indicator
import java.util.*

/**
 * Created by Jack on 2015/10/19.
 */
open class BallScaleMultipleIndicator : Indicator() {

    internal var scaleFloats = floatArrayOf(1f, 1f, 1f)
    internal var alphaInts = intArrayOf(255, 255, 255)

    override fun draw(canvas: Canvas, paint: Paint) {
        val circleSpacing = 4f
        for (i in 0..2) {
            paint.alpha = alphaInts[i]
            canvas.scale(scaleFloats[i], scaleFloats[i], centerX().toFloat(), centerY().toFloat())
            canvas.drawCircle(centerX().toFloat(), centerY().toFloat(), width / 2 - circleSpacing, paint)
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val delays = longArrayOf(0, 200, 400)
        for (i in 0..2) {
            val scaleAnim = ValueAnimator.ofFloat(0f, 1f)
            scaleAnim.interpolator = LinearInterpolator()
            scaleAnim.duration = 1000
            scaleAnim.repeatCount = -1
            addUpdateListener(scaleAnim) { animation ->
                scaleFloats[i] = animation.animatedValue as Float
                postInvalidate()
            }
            scaleAnim.startDelay = delays[i]

            val alphaAnim = ValueAnimator.ofInt(255, 0)
            alphaAnim.interpolator = LinearInterpolator()
            alphaAnim.duration = 1000
            alphaAnim.repeatCount = -1
            addUpdateListener(alphaAnim) { animation ->
                alphaInts[i] = animation.animatedValue as Int
                postInvalidate()
            }
            scaleAnim.startDelay = delays[i]

            animators.add(scaleAnim)
            animators.add(alphaAnim)
        }
        return animators
    }

}