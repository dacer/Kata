package im.dacer.kata.core.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.atilika.kuromoji.ipadic.Token
import im.dacer.kata.core.extension.toKanjiResult
import im.dacer.kata.core.util.ViewUtil
import im.dacer.kata.segment.model.KanjiResult


/**
 * Created by Dacer on 20/01/2018.
 */
class FuriganaView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var kanjiResult: KanjiResult? = null

    private val furiganaPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val normalPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var topMargin: Int = ViewUtil.dpToPx(2)
    private val furiganaHeight: Float get() = furiganaPaint.fontMetrics.let { it.bottom - it.top }
    private var furiganaBottomMargin: Int = ViewUtil.dpToPx(0)

    private val normalHeight: Float get() = normalPaint.fontMetrics.let { it.bottom - it.top }
    private var bottomMargin: Int = ViewUtil.dpToPx(2)

    private val furiganaContainerWidth: Float get() = kanjiResult?.run { getFuriganaContainerWidth(this) } ?: 0f
    private val normalWidth: Float get() = normalPaint.measureText(kanjiResult?.surface ?: "")

    var showFurigana: Boolean = true
        set(value) {
            field = value
            postInvalidate()
        }

    init {
        furiganaPaint.color = GRAY
        normalPaint.color = Color.BLACK

        furiganaPaint.textAlign = Paint.Align.CENTER
        normalPaint.textAlign = Paint.Align.CENTER

//        if (attrs != null) {
//            val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BigBangLayout)
//            val textSize = typedArray.getDimensionPixelSize(R.styleable.BigBangLayout_textSize, resources.getDimensionPixelSize(R.dimen.big_bang_default_text_size))
//            setTextSpSize(textSize)
//            typedArray.recycle()
//        }
    }

    fun setTextSpSize(sizeInSp: Float) {
        normalPaint.textSize = ViewUtil.sp2px(sizeInSp)
        requestLayout()
        invalidate()
    }

    fun setFuriganaTextSpSize(sizeInSp: Float) {
        furiganaPaint.textSize = ViewUtil.sp2px(sizeInSp)
        requestLayout()
        invalidate()
    }

    fun setText(kanjiResult: KanjiResult): FuriganaView {
        this.kanjiResult = kanjiResult
        return this
    }

    fun setText(token: Token) {
        setText(token.toKanjiResult())
    }

    /**
     * return new line count
     */
    fun isNewLine(): Int {
        val surface = kanjiResult?.surface ?: ""
        if (surface.replace("\n", "").isNotBlank()) return 0
        return surface.count { it == '\n' }
    }

    fun isBlank(): Boolean {
        val surface = kanjiResult?.surface ?: ""
        return surface.replace("\n", "").isBlank()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        normalPaint.color = if (selected) RED else Color.BLACK
        furiganaPaint.color = if (selected) RED else GRAY
    }

    public override fun onDraw(canvas: Canvas) {
        if (kanjiResult == null) return
        val xPos = canvas.width / 2f
        var xOffset = 0f
        val surface = kanjiResult?.surface ?: ""

        if (kanjiResult!!.needShowFurigana && (showFurigana || isSelected)) {
            val furiXPos = getFuriganaXpos(kanjiResult!!)
            val furiWidth = furiganaPaint.measureText(kanjiResult!!.furiganaForDisplay)

            if (furiXPos < furiWidth / 2) {
                xOffset = furiWidth / 2 - furiXPos
            }
            // since none of 動詞 is end of 漢字, so this If Expression will never be reached
//            else if (width - furiXPos < furiWidth / 2) {
//                xOffset = furiWidth / 2 - width + furiXPos
//            }

            canvas.drawText(kanjiResult!!.furiganaForDisplay,
                    furiXPos + xOffset,
                    (topMargin - furiganaPaint.fontMetrics.top), furiganaPaint)
        }
        canvas.drawText(surface, xPos + xOffset,
                (topMargin + furiganaHeight + furiganaBottomMargin - normalPaint.fontMetrics.top),
                normalPaint)
    }

    /**
     * e.g.
     * |←　　　→|
     * |かんが　|
     * |　考　え|
     */
    private fun getFuriganaContainerWidth(k: KanjiResult): Float {
        val furiWidth = furiganaPaint.measureText(k.furiganaForDisplay)
        val halfMainSurface = normalPaint.measureText(k.surface.substring(k.furiganaStartOffset, k.surface.length - k.furiganaEndOffset)) / 2
        val startWidth = normalPaint.measureText(k.surface.substring(0, k.furiganaStartOffset))
        val endWidth = normalPaint.measureText(k.surface.substring(k.surface.length - k.furiganaEndOffset, k.surface.length))

        return Math.max(furiWidth, furiWidth / 2 + Math.max(halfMainSurface + startWidth, halfMainSurface + endWidth))
    }

    private fun getFuriganaXpos(k: KanjiResult): Float {
        return (width - normalPaint.measureText(k.surface)) / 2 +
                normalPaint.measureText(k.surface.substring(0, k.furiganaStartOffset)) +
                normalPaint.measureText(k.surface.substring(k.furiganaStartOffset, k.surface.length - k.furiganaEndOffset)) / 2
    }

    override fun onMeasure(width_ms: Int, height_ms: Int) {
        var width = Math.max(furiganaContainerWidth, normalWidth).toInt()
        var height = (topMargin + furiganaHeight + furiganaBottomMargin + normalHeight + bottomMargin).toInt()
        val newLineCount = isNewLine()
        if (newLineCount > 0) {
            width = (parent as View).width
            if (newLineCount == 1) height = 0
        }
        setMeasuredDimension(width, height)
    }

    companion object {
        val RED = Color.parseColor("#F44336")
        val GRAY = Color.parseColor("#424242")
    }
}