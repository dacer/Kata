package im.dacer.kata.view

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.ImageView
import timber.log.Timber


class GradientImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ImageView(context, attrs, defStyleAttr){
    private var linearGradient: LinearGradient? = null
    private val paint = Paint()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Timber.e("$w, $h")
        linearGradient = LinearGradient(w.toFloat(), 0f, 0f, 0f,
                COLOR_GRADIENT_START, COLOR_GRADIENT_END, Shader.TileMode.CLAMP)
        paint.shader = linearGradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Timber.e("onDraw: $width, $height")
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    companion object {
        val COLOR_GRADIENT_START = Color.parseColor("#c84b4a47")
        val COLOR_GRADIENT_END = Color.parseColor("#4b4a47")
    }
}