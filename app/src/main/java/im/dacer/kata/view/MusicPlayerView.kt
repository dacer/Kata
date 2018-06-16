package im.dacer.kata.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Align
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import com.daasuu.ei.Ease
import com.daasuu.ei.EasingInterpolator
import com.devbrackets.android.exomedia.AudioPlayer
import im.dacer.kata.R
import im.dacer.kata.util.LogUtils
import im.dacer.kata.view.indicator.BallScaleMultipleIndicator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.dip
import org.jetbrains.anko.sp
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit


class MusicPlayerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val audioPlayer: AudioPlayer by lazy { AudioPlayer(context) }
    private val btnDrawable = resources.getDrawable(R.drawable.floating_empty_button)
    private val gestureDetector: GestureDetector = GestureDetector(context, MyGestureDetector())

    private val btnTextPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val playPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val leftMainTextPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val leftSubTextPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val textBackgroundPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val playPath: Path = Path()
    private var leftSubTextRect: Rect? = null
    private var leftMainTextRect: Rect? = null
    private val loadingDrawable = BallScaleMultipleIndicator()

    private var posProcess: Float = 0f   // position process [0 - 1]
    private var initProcess: Float = 0f
    private var updateProcessDisposable: Disposable? = null
    private var textInBtnCenter: String? = null
    private var initAnim: ValueAnimator? = null
    private var playerPrepared = false
    private var audioUrl: String? = null
    private var mShouldStartAnimationDrawable = false
    private var loadingAnimIsRunning = false


    init {
        btnTextPaint.textSize = sp(18).toFloat()
        btnTextPaint.textAlign = Align.CENTER
        btnTextPaint.color = Color.WHITE

        leftMainTextPaint.textSize = sp(50).toFloat()
        leftMainTextPaint.textAlign = Align.CENTER
        leftMainTextPaint.color = Color.WHITE

        leftSubTextPaint.textSize = sp(18).toFloat()
        leftSubTextPaint.textAlign = Align.CENTER
        leftSubTextPaint.color = Color.WHITE

        textBackgroundPaint.color = Color.parseColor("#c8000000")

        playPaint.color = Color.WHITE
        playPaint.style = Paint.Style.FILL_AND_STROKE

        loadingDrawable.callback = this
        loadingDrawable.color = Color.WHITE
    }

    fun setDataSource(voiceUrl: String, prepareImmediately: Boolean = true) {
        audioUrl = voiceUrl
        if (prepareImmediately) {
            prepare()
        } else {
            postInvalidate()
        }
    }

    private fun prepare() {
        playerPrepared = false
        startLoadingAnim()
        audioPlayer.setOnPreparedListener {
            playerPrepared = true
            stopLoadingAnim()
            show()
        }
        audioPlayer.setOnBufferUpdateListener {
            val isLoading = it <= 100 * getProcessByPlayerCurrentPos()
            if (isLoading) {
                if (!playerPrepared) return@setOnBufferUpdateListener
                playerPrepared = false
                startLoadingAnim()
            } else {
                if (playerPrepared) return@setOnBufferUpdateListener
                playerPrepared = true
                stopLoadingAnim()
            }
            
//            Timber.e("buffer: $it, percent: ${getProcessByPlayerCurrentPos()}, isLoading ->  $isLoading")
        }
        audioPlayer.setDataSource(Uri.parse(audioUrl))
        show()
    }

    fun show() {
        if (initProcess > 0) return
        initAnim?.end()

        initAnim = ValueAnimator.ofFloat(0f, 1f)
        initAnim?.duration = 300
        initAnim?.interpolator = EasingInterpolator(Ease.BACK_OUT)
        initAnim?.addUpdateListener {
            initProcess = it.animatedValue as Float
            postInvalidate()
        }
        initAnim?.start()
    }

    fun hide() {
        if (initProcess < 1) return
        initAnim?.end()

        initAnim = ValueAnimator.ofFloat(1f, 0f)
        initAnim?.duration = 200
        initAnim?.addUpdateListener {
            initProcess = it.animatedValue as Float
            postInvalidate()
        }
        initAnim?.start()
    }

    fun play() {
        audioPlayer.setOnCompletionListener {
            stopUpdateProcess()
            textInBtnCenter = null
            invalidate()
        }
        audioPlayer.start()
        startUpdateProcess()
    }

    fun pause() {
        audioPlayer.pause()
        stopUpdateProcess()
    }

    private fun startUpdateProcess() {
        if (!isPlaying()) return
        updateProcessDisposable?.dispose()
        updateProcessDisposable = Observable.interval(50, TimeUnit.MILLISECONDS)
                .doOnNext {
                    posProcess = getProcessByPlayerCurrentPos()
                    textInBtnCenter = audioPlayer.currentPosition.toMmSs()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    invalidate()
                }, { LogUtils.log(it) })
    }

    private fun stopUpdateProcess() {
        if (updateProcessDisposable?.isDisposed != true) updateProcessDisposable?.dispose()
    }

    private fun isPlaying(): Boolean = audioPlayer.isPlaying

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (audioUrl.isNullOrEmpty()) return

        btnDrawable.bounds = getBtnBounds()
        btnDrawable.draw(canvas)
        if (textInBtnCenter.isNullOrEmpty()) {
            if (!loadingAnimIsRunning) {
                drawPlayTriangle(canvas)
            }
        } else {
            canvas.drawRectText(textInBtnCenter!!, getBtnBounds(), btnTextPaint)
            if (keepOnTouch) {
                canvas.drawRect(leftMainTextRect, textBackgroundPaint)
                canvas.drawRect(leftSubTextRect, textBackgroundPaint)

                canvas.drawRectText((audioPlayer.duration * posProcess).toLong().toMmSs(),
                        leftMainTextRect!!, leftMainTextPaint)
                canvas.drawRectText(textInBtnCenter!!,
                        leftSubTextRect!!, leftSubTextPaint)
            }
        }
        if (loadingAnimIsRunning) {
            drawLoading(canvas)
        }
    }

    private var keepOnTouch = false
    private var freeMoveMode = false
    private var freeModeX = 0
    private var freeModeY = 0
    private var actionDownX = 0f
    private var actionDownY = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (audioUrl.isNullOrEmpty()) return false
        val insideBtn = getBtnBounds().contains(event.x.toInt(), event.y.toInt())

        if (keepOnTouch || insideBtn) {
            val result = gestureDetector.onTouchEvent(event)
            if (result) { return result }
            if (!isPlaying() && posProcess == 0f) return true

            when (event.action) {
                ACTION_MOVE -> {
                    if (freeMoveMode) {
                        freeModeX = event.x.toInt()
                        freeModeY = event.y.toInt()

                    } else {
                        if (canIgnoreThisTouch(event) && !keepOnTouch) return true

                        if (event.x < width / 3 * 2) {
                            stopUpdateProcess()
                            freeMoveMode = true
                            return true
                        }

                        posProcess = 1 - (event.y - paddingTop) / (actualHeight -  btnDrawable.intrinsicHeight / 2f)
                        posProcess = posProcess.coerceIn(0f, 1f)
                        keepOnTouch = true
                    }
                    invalidate()
                }
                ACTION_UP, ACTION_CANCEL -> {
                    val playerProcess = getProcessByPlayerCurrentPos()
                    if (playerProcess != posProcess) {
                        audioPlayer.seekTo((audioPlayer.duration * posProcess).toLong())
                        textInBtnCenter = audioPlayer.currentPosition.toMmSs()
                        invalidate()
                    }
                    doOnTouchUp()
                }
            }
            return true
        }

        if (event.action == ACTION_MOVE) {
            keepOnTouch = true
        }
        if (event.action == ACTION_UP || event.action == ACTION_CANCEL) { doOnTouchUp() }
        return super.onTouchEvent(event)
    }

    private fun doOnTouchUp() {
        keepOnTouch = false
        freeMoveMode = false
        startUpdateProcess()
        invalidate()
    }

    private fun canIgnoreThisTouch(event: MotionEvent): Boolean {
        return Math.abs(actionDownX - event.x) < 5 ||
                Math.abs(actionDownY - event.y) < 5
    }

    override fun onDetachedFromWindow() {
        audioPlayer.stopPlayback()
        updateProcessDisposable?.dispose()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateTextBounds()
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
            l = actualWidth - btnWidth / 3 - (btnWidth / 3 * 2 * initProcess).toInt()
            t = (paddingTop + (actualHeight -  btnHeight) * (1 - posProcess)).toInt()
        }
        return Rect(l, t, l + btnWidth, t + btnHeight)
    }

    private fun updateTextBounds() {
        val mainHeight = dip(70)
        val subHeight = dip(30)
        leftMainTextRect = Rect(0,
                height / 2 - mainHeight / 2,
                dip(140),
                height / 2 + mainHeight / 2)
        leftSubTextRect = Rect(0,
                height / 2 - mainHeight / 2 - dip(5) - subHeight,
                dip(80),
                height / 2 - mainHeight / 2 - dip(5))
    }

    private inner class MyGestureDetector : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            actionDownX = e.x
            actionDownY = e.y
            stopUpdateProcess()
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if ((initProcess < 1f || !playerPrepared || loadingAnimIsRunning)) {
                when {
                    playerPrepared -> show()
                    audioPlayer.bufferPercentage > 0 || loadingAnimIsRunning -> context.toast(R.string.voice_file_is_loading)
                    else -> prepare()
                }
                return true
            }

            if (isPlaying()) {
                pause()
            } else {
                play()
            }
            return true
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

    private fun getProcessByPlayerCurrentPos(): Float {
        return audioPlayer.currentPosition.run {if (this == 0L) { 0f } else { toFloat() / audioPlayer.duration } }
    }

    private fun Canvas.drawRectText(text: String, r: Rect, paint: Paint) {
        val numOfChars = paint.breakText(text, true, width.toFloat(), null)
        val start = (text.length - numOfChars) / 2
        drawText(text, start, start + numOfChars, r.exactCenterX(), r.exactCenterY() -
                (paint.descent() + paint.ascent()) / 2, paint)
    }

    private fun startLoadingAnim() {
        loadingAnimIsRunning = true
        mShouldStartAnimationDrawable = true
        postInvalidate()
    }

    private fun stopLoadingAnim() {
        loadingAnimIsRunning = false
        loadingDrawable.stop()
        mShouldStartAnimationDrawable = false
        postInvalidate()
    }

    private fun drawLoading(canvas: Canvas) {
        loadingDrawable.bounds = getBtnBounds()
        loadingDrawable.draw(canvas)

        if (mShouldStartAnimationDrawable) {
            loadingDrawable.start()
            mShouldStartAnimationDrawable = false
        }
    }
    private fun drawPlayTriangle(c: Canvas) {
        c.drawPath(getTrianglePath(playPath), playPaint)
    }

    private fun getTrianglePath(path: Path): Path {
        path.reset()
        val cirX = getBtnBounds().centerX()
        val cirY = getBtnBounds().centerY()
        val cirInnerRadius = getBtnBounds().width() / 2.2f
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

    override fun invalidateDrawable(drawable: Drawable?) {
        super.invalidateDrawable(drawable)
        postInvalidate()
    }
}