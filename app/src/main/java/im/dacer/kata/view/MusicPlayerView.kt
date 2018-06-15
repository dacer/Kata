package im.dacer.kata.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Align
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.animation.BounceInterpolator
import com.devbrackets.android.exomedia.AudioPlayer
import im.dacer.kata.R
import im.dacer.kata.util.LogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.sp
import java.util.concurrent.TimeUnit


class MusicPlayerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val audioPlayer: AudioPlayer by lazy { AudioPlayer(context) }
    private val btnDrawable = resources.getDrawable(R.drawable.floating_empty_button)
    private val gestureDetector: GestureDetector = GestureDetector(context, MyGestureDetector())

    private val textPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val playPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val playPath: Path = Path()

    private var process: Float = 0f
    private var initProcess: Float = 0f
    private var updateProcessDisposable: Disposable? = null
    private var textInCenter: String? = null

    init {
        textPaint.textSize = sp(18).toFloat()
        textPaint.textAlign = Align.CENTER
        textPaint.color = Color.WHITE

        playPaint.color = Color.WHITE
        playPaint.style = Paint.Style.FILL_AND_STROKE
    }

    fun setDataSource(voiceUrl: String) {
        audioPlayer.setOnPreparedListener {
            val initAnim = ValueAnimator.ofFloat(0f, 1f)
            initAnim.duration = 1000
            initAnim.interpolator = BounceInterpolator()
            initAnim.addUpdateListener {
                initProcess = it.animatedValue as Float
                postInvalidate()
            }
            initAnim.start()
        }
        audioPlayer.setDataSource(Uri.parse(voiceUrl))
    }

    fun play() {
        audioPlayer.setOnCompletionListener {
            stopUpdateProcess()
            textInCenter = null
            invalidate()
        }
        audioPlayer.start()
        startUpdateProcess()
    }

    private fun startUpdateProcess() {
        if (!isPlaying()) return
        updateProcessDisposable?.dispose()
        updateProcessDisposable = Observable.interval(50, TimeUnit.MILLISECONDS)
                .map { audioPlayer.currentPosition }
                .doOnNext {
                    process = if (it == 0L) { 0f } else { it.toFloat() / audioPlayer.duration }
                    textInCenter = it.toMmSs()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    invalidate()
                }, { LogUtils.log(it) })
    }

    private fun stopUpdateProcess() {
        updateProcessDisposable?.dispose()
    }

    private fun isPlaying(): Boolean = audioPlayer.isPlaying

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        btnDrawable.bounds = getBtnBounds()
        btnDrawable.draw(canvas)
        if (textInCenter.isNullOrEmpty()) {
            drawPlayTriangle(canvas)
        } else {
            canvas.drawRectText(textInCenter!!, getBtnBounds())
        }
    }

    private var keepOnTouch = false
    private var freeMoveMode = false
    private var freeModeX = 0
    private var freeModeY = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (initProcess < 1f) return false
        if (keepOnTouch || getBtnBounds().contains(event.x.toInt(), event.y.toInt())) {
            val result = gestureDetector.onTouchEvent(event)
            if (result) { return result }
            if (!isPlaying() && process == 0f) return false

            when (event.action) {
                ACTION_MOVE -> {
                    if (freeMoveMode) {
                        freeModeX = event.x.toInt()
                        freeModeY = event.y.toInt()

                    } else {
                        if (event.x < width / 3 * 2) {
                            stopUpdateProcess()
                            freeMoveMode = true
                            return true
                        }
                        process = 1 - (event.y - paddingTop) / (actualHeight -  btnDrawable.intrinsicHeight / 2f)
                        process = process.coerceIn(0f, 1f)
                        keepOnTouch = true
                    }
                    invalidate()
                }
                ACTION_UP, ACTION_CANCEL -> {
                    keepOnTouch = false
                    freeMoveMode = false
                    startUpdateProcess()

                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDetachedFromWindow() {
        audioPlayer.stopPlayback()
        updateProcessDisposable?.dispose()
        super.onDetachedFromWindow()
    }

    private fun getBtnBounds(): Rect {
        val btnWidth = btnDrawable.intrinsicWidth
        val btnHeight = btnDrawable.intrinsicHeight
        val l: Int
        val t: Int
        if (freeMoveMode) {
            l = freeModeX - btnWidth / 2
            t = freeModeY - btnHeight / 2
        } else {
            l = actualWidth - (btnWidth * initProcess).toInt()
            t = (paddingTop + (actualHeight -  btnHeight) * (1 - process)).toInt()
        }
        return Rect(l, t, l + btnWidth, t + btnHeight)
    }

    private inner class MyGestureDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            play()
            return super.onSingleTapConfirmed(e)
        }
    }

    private var actualHeight: Int = 0
        get() = height - paddingTop - paddingBottom

    private var actualWidth: Int = 0
        get() = width - paddingLeft - paddingRight

    private fun Long.toMmSs(): String {
        val s = this / 1000 % 60
        val m = this / 1000 / 60 % 60
        return String.format("%d:%02d", m, s)
    }

    private fun Canvas.drawRectText(text: String, r: Rect) {
        val numOfChars = textPaint.breakText(text, true, width.toFloat(), null)
        val start = (text.length - numOfChars) / 2
        drawText(text, start, start + numOfChars, r.exactCenterX(), r.exactCenterY() - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
    }

    private fun drawPlayTriangle(c: Canvas) {
        c.drawPath(getTrianglePath(playPath), playPaint)
    }

    private fun getTrianglePath(path: Path): Path {
        path.reset()
        val cirX = getBtnBounds().centerX()
        val cirY = getBtnBounds().centerY()
        val cirInnerRadius = getBtnBounds().width() / 2f
        val triangleSideLength = cirInnerRadius * 2 / 3

        val tan30 = Math.tan(Math.toRadians(30.0))
        val cos30 = Math.cos(Math.toRadians(30.0))
        val leftX = cirX - (triangleSideLength / 2 * tan30).toFloat()
        val leftUpY = cirY - triangleSideLength / 2
        val leftBottomY = cirY + triangleSideLength / 2
        val rightX = cirX + (triangleSideLength.toDouble() / 2.0 / cos30).toFloat()
        val rightY = cirY
        path.moveTo(leftX, leftUpY)
        path.lineTo(leftX, leftBottomY)
        path.lineTo(rightX, rightY.toFloat())
        path.lineTo(leftX, leftUpY)
        path.close()
        return path
    }
}