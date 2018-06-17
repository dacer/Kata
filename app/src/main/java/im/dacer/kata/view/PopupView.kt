package im.dacer.kata.view

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import im.dacer.kata.R
import im.dacer.kata.util.ViewUtil
import org.jetbrains.anko.dip


/**
 * Created by Dacer on 16/01/2018.
 * A Copy Popup
 */
class PopupView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val popupTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var point: Point? = null
    private val popupStr: String by lazy { context.getString(R.string.copy) }
    private val popupHeight = dip(30)
    private val popUpWidth = dip(50)
    private val arrowHeight = dip(10)
    private val arrowWidth = dip(12)

    var listener: PopupListener? = null

    init {
        popupTextPaint.color = Color.WHITE
        popupTextPaint.textSize = ViewUtil.sp2px(16f)
        popupTextPaint.strokeWidth = 5f
        popupTextPaint.textAlign = Paint.Align.CENTER
        redPaint.color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        point?.run { drawPopup(canvas, this) }
    }

    val popupIsShown: Boolean get() { return point != null }

    fun show(point: Point) {
        this.point = point
        postInvalidate()
    }

    fun hide() {
        this.point = null
        postInvalidate()
    }

    private fun drawPopup(canvas: Canvas, point: Point) {
        canvas.drawRoundRect(getBtnBounds(point), dip(6).toFloat(), dip(6).toFloat(), redPaint)
        canvas.drawPath(getArrowPath(point), redPaint)
        canvas.drawRectText(popupStr, getBtnBounds(point), popupTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (getBtnBounds(point).expandBounds(dip(20)).contains(event.x, event.y)) {
                listener?.onPopupClicked()
            }
            hide()
        }
        return popupIsShown
    }

    private fun getArrowPath(point: Point): Path {
        val result = Path()
        result.moveTo(point.x.toFloat() - arrowWidth/2, point.y.toFloat())
        result.lineTo(point.x.toFloat() - arrowWidth/2, getBtnBounds(point).bottom)
        result.lineTo(point.x.toFloat() + arrowWidth/2, getBtnBounds(point).bottom)
        result.lineTo(point.x.toFloat() - arrowWidth/2, point.y.toFloat())
        return result
    }

    private fun getBtnBounds(point: Point?): RectF {
        if (point == null) return RectF()
        return RectF(point.x - popUpWidth / 2f,
                (point.y - popupHeight).toFloat() - arrowHeight,
                point.x + popUpWidth / 2f,
                point.y.toFloat() - arrowHeight)
    }

    private fun RectF.expandBounds(px: Int): RectF {
        return RectF(left - px, top - px, right + px, bottom + px)
    }

    private fun Canvas.drawRectText(text: String, r: RectF, paint: Paint) {
        val numOfChars = paint.breakText(text, true, width.toFloat(), null)
        val start = (text.length - numOfChars) / 2
        drawText(text, start, start + numOfChars, r.centerX(), r.centerY() -
                (paint.descent() + paint.ascent()) / 2, paint)
    }

    interface PopupListener {
        fun onPopupClicked()
    }

}