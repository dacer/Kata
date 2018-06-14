package im.dacer.kata.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Align
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
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

    private var process: Float = 0f
    private var updateProcessDisposable: Disposable? = null
    private var textInCenter: String? = null

    init {
        textPaint.textSize = sp(20).toFloat()
        textPaint.textAlign = Align.CENTER
        textPaint.color = Color.WHITE
    }

    fun setDataSource(voiceUrl: String) {
        audioPlayer.setOnPreparedListener{
            // todo animation
        }
        audioPlayer.setDataSource(Uri.parse(voiceUrl))
    }

    fun play() {
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

        audioPlayer.setOnCompletionListener {
            updateProcessDisposable?.dispose()
            textInCenter = null
            invalidate()
        }
        audioPlayer.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        btnDrawable.bounds = getBtnBounds()
        btnDrawable.draw(canvas)
        if (textInCenter.isNullOrEmpty()) {
            // draw play icon
        } else {
            canvas.drawRectText(textInCenter!!, getBtnBounds())
        }
    }

    private var keepOnTouch = false
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (keepOnTouch || getBtnBounds().contains(event.x.toInt(), event.y.toInt())) {
            val result = gestureDetector.onTouchEvent(event)
            if (result) { return result }

            when (event.action) {
                ACTION_MOVE -> {
                    process = 1 - (event.y - paddingTop) / (actualHeight -  btnDrawable.intrinsicHeight / 2f)
                    process = process.coerceIn(0f, 1f)
                    invalidate()
                    keepOnTouch = true
                }
                ACTION_UP, ACTION_CANCEL -> {
                    keepOnTouch = false
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
        val left = actualWidth - btnDrawable.intrinsicWidth
        val top = (paddingTop + (actualHeight -  btnDrawable.intrinsicHeight) * (1 - process)).toInt()

        return Rect(left, top, left + btnDrawable.intrinsicWidth, top + btnDrawable.intrinsicHeight)
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
}