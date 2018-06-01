package im.dacer.kata.core.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.atilika.kuromoji.ipadic.Token
import im.dacer.kata.core.extension.toKanjiResult
import im.dacer.kata.core.util.ViewUtil
import im.dacer.kata.segment.model.KanjiResult

/**
 * Created by Dacer on 20/01/2018.
 */
class KataLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var lineSpace: Int = 0
        set(value) {
            field = ViewUtil.dpToPx(value)
            requestLayout()
        }
    var itemSpace: Int = 0
        set(value) {
            field = ViewUtil.dpToPx(value)
            requestLayout()
        }
    var itemTextSize: Float = 10f
        set(value) {
            field = value
            (0 until childCount)
                    .map { getChildAt(it) }
                    .forEach { (it as? FuriganaView)?.setTextSpSize(itemTextSize) }
        }
    var itemFuriganaTextSize: Float = 10f
        set(value) {
            field = value
            (0 until childCount)
                    .map { getChildAt(it) }
                    .forEach { (it as? FuriganaView)?.setFuriganaTextSpSize(itemFuriganaTextSize) }
        }
    var showFurigana = true
    var itemClickListener: ItemClickListener? = null

    private var preselectedIndex = -1
    private var mLines: MutableList<Line> = arrayListOf()
    private var mScaledTouchSlop: Int = ViewConfiguration.get(getContext()).scaledTouchSlop

    fun showFurigana(show: Boolean) {
        showFurigana = show
        mLines
                .map { it.itemList }
                .flatMap { it }
                .forEach { it.view.showFurigana = show }
    }

    fun setTokenData(tokens: List<Token>) {
        setKanjiResultData(tokens.map { it.toKanjiResult() })
    }

    fun select(index: Int) {
        preselectedIndex = index
        var i = 0
        loop@ for (line in mLines) {
            for (item in line.itemList) {
                if (preselectedIndex == i) {
                    onItemSelected(item)
                    preselectedIndex = -1
                    break@loop
                }
                i++
            }
        }
    }

    fun setKanjiResultData(kanjiResults: List<KanjiResult>) {
        kanjiResults.forEach {
            val view = FuriganaView(context)
            view.setText(it)
            view.showFurigana = showFurigana
            if (itemTextSize > 0) view.setTextSpSize(itemTextSize)
            if (itemFuriganaTextSize > 0) view.setFuriganaTextSpSize(itemFuriganaTextSize)
            addView(view)
        }
    }

    fun reset() {
        removeAllViews()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top: Int
        var left: Int
        val offsetTop = 0
        var newLineCount = 0f

        for (i in mLines.indices) {
            val line = mLines[i]
            val items = line.itemList
            left = paddingLeft


            for (j in items.indices) {
                val item = items[j]
                top = (paddingTop + (i - newLineCount) * (item.height + lineSpace) + offsetTop).toInt()
                val child = item.view
//                val oldTop = child.top

                // \n
                if (child.isNewLine() == 1) {
                    newLineCount += 1
                }

                // \n\n 's height is 0.7 * normal view height
                if (child.isNewLine() > 1) {
                    newLineCount += (1 - EMPTY_LINE_RATIO)
                }

                child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)

//                if (oldTop != top) {
//                    val translationY = oldTop - top
//                    child.translationY = translationY.toFloat()
//                    child.animate().translationYBy((-translationY).toFloat()).setDuration(200).start()
//                }
                left += child.measuredWidth + itemSpace
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        var newLineCount = 0f
        var childHeight = 0

        mLines.clear()
        var currentLine: Line? = null
        var currentLineWidth = widthSize

        for (i in 0 until childCount) {
            val child = getChildAt(i) as FuriganaView
            child.measure(measureSpec, measureSpec)

            if (currentLineWidth > 0) {
                currentLineWidth += itemSpace
            }
            currentLineWidth += child.measuredWidth
            if (mLines.size == 0 || currentLineWidth > widthSize) {
                if (child.isNewLine() > 1) {
                    newLineCount += 1 - EMPTY_LINE_RATIO
                } else if (child.isNewLine() == 1) {
                    newLineCount += 1
                } else {
                    childHeight = child.measuredHeight
                }
                currentLineWidth = child.measuredWidth
                currentLine = Line()
                mLines.add(currentLine)
            }
            val item = Item(child)
            item.index = i
            item.width = child.measuredWidth
            item.height = child.measuredHeight
            currentLine!!.addItem(item)
        }

        if (mLines.isNotEmpty() && preselectedIndex != -1) select(preselectedIndex)
        val size = (mLines.size - newLineCount) * childHeight + paddingTop + paddingBottom + (mLines.size - 1 - newLineCount) * lineSpace
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(size.toInt(), View.MeasureSpec.EXACTLY))
    }

    private var lastSelectedItem : Item? = null
    private var actionDownItem: Item? = null
    private var mDisallowedParentIntercept = false
    private var mDownX: Float = 0F
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDisallowedParentIntercept = false
                actionDownItem = findItemByPoint(event.x.toInt(), event.y.toInt())
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mDisallowedParentIntercept && Math.abs(event.x - mDownX) > mScaledTouchSlop) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    mDisallowedParentIntercept = true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mDisallowedParentIntercept) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                val item = findItemByPoint(event.x.toInt(), event.y.toInt())
                if (item != null && !item.view.isBlank()) {
                    onItemSelected(item)
                }
            }
        }
        return true
    }

    private fun onItemSelected(item: Item) {
        lastSelectedItem?.isSelected = false
        item.isSelected = true
        lastSelectedItem = item
        itemClickListener?.onItemClicked(item.index)
    }

    private fun findItemByPoint(x: Int, y: Int): Item? {
        return mLines
                .map { it.itemList }
                .flatMap { it }
                .firstOrNull { it.rect.contains(x, y) }
    }

    internal class Line {
        var itemList: MutableList<Item> = arrayListOf()

        val height: Int
            get() {
                return if (itemList.isNotEmpty()) {
                    itemList[0].view.measuredHeight
                } else 0
            }

        fun addItem(item: Item): Line {
            itemList.add(item)
            return this
        }

    }

    internal class Item(var view: FuriganaView, var height: Int = 0) {
        var index: Int = 0
        var width: Int = 0

        val rect: Rect
            get() {
                val rect = Rect()
                view.getHitRect(rect)
                return rect
            }

        var isSelected: Boolean
            get() = view.isSelected
            set(selected) {
                view.isSelected = selected
            }
    }

    interface ItemClickListener {
        fun onItemClicked(index: Int)
    }

    companion object {
        // \n\n 's height is 0.7 * normal view height
        const val EMPTY_LINE_RATIO = 0.7f
    }
}