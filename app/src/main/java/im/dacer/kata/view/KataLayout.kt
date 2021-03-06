package im.dacer.kata.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import com.afollestad.materialdialogs.MaterialDialog
import im.dacer.kata.R
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.util.ViewUtil
import im.dacer.kata.util.extension.copyToClipboard
import im.dacer.kata.util.helper.SchemeHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.math.abs


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
                .flatten()
                .forEach { it.view.showFurigana = show }
    }

    fun select(index: Int) {
        preselectedIndex = index
        if (index < 0) return
        loop@ for (line in mLines) {
            for (item in line.itemList) {
                if (preselectedIndex == item.index) {
                    onItemSelected(item, false)
                    preselectedIndex = -1
                    break@loop
                }
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
        var top = paddingTop
        var left: Int

        for (i in mLines.indices) {
            val line = mLines[i]
            val items = line.itemList
            left = paddingLeft

            for (j in items.indices) {
                val item = items[j]
                val child = item.view

                child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)
                left += child.measuredWidth + itemSpace
            }
            if (items.isNotEmpty()) top += items.first().view.measuredHeight + lineSpace
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val measureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        var childHeight = 0
        var totalHeight = 0

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
                childHeight = child.measuredHeight
                currentLineWidth = child.measuredWidth
                currentLine = Line()
                mLines.add(currentLine)
                totalHeight += childHeight + lineSpace
            }
            val item = Item(child)
            item.index = i
            item.width = child.measuredWidth
            item.height = child.measuredHeight
            currentLine!!.addItem(item)
        }

        if (mLines.isNotEmpty() && preselectedIndex != -1) select(preselectedIndex)
        val size = totalHeight + paddingTop + paddingBottom
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY))
    }

    private var lastSelectedItem : Item? = null
    private var actionDownItem: Item? = null
    private var mDisallowedParentIntercept = false
    private var mDownX: Float = 0F
    private var longClickDisposable: Disposable? = null
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDisallowedParentIntercept = false
                actionDownItem = findItemByPoint(event.x.toInt(), event.y.toInt())
                longClickDisposable?.dispose()
                longClickDisposable = Observable.timer(800, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            onItemLongClicked(actionDownItem)
                        }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mDisallowedParentIntercept && abs(event.x - mDownX) > mScaledTouchSlop) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    mDisallowedParentIntercept = true
                    longClickDisposable?.dispose()
                }
            }
            MotionEvent.ACTION_UP -> {
                longClickDisposable?.dispose()
                if (mDisallowedParentIntercept) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                val item = findItemByPoint(event.x.toInt(), event.y.toInt())
                if (item?.isSelected == true) {
                    //do nothing
                } else if (item != null && !item.view.isBlank()) {
                    onItemSelected(item)
                }
            }
            else -> {
                longClickDisposable?.dispose()
            }
        }
        return true
    }

    private fun onItemSelected(item: Item, selectedByUser: Boolean = true) {
        if (item.view.isUrl && item.view.surface != null) {
            onClickLink(item)
            return
        }
        lastSelectedItem?.isSelected = false
        item.isSelected = true
        lastSelectedItem = item
        itemClickListener?.onItemClicked(item.index, selectedByUser)
    }

    private fun onItemLongClicked(item: Item?) {
        MaterialDialog.Builder(context)
                .items(arrayOf(R.string.copy).map { context.getString(it) })
                .itemsCallback{_, _, pos, _ ->
                    when(pos) {
                        0 -> item?.view?.surface?.copyToClipboard(context, false)
                    }
                }
                .show()
    }

    private fun onClickLink(item: Item) {
        MaterialDialog.Builder(context)
                .title(item.view.surface!!)
                .items(arrayOf(R.string.parse_content, R.string.open_in_browser).map { context.getString(it) })
                .itemsCallback{_, _, pos, _ ->
                    when(pos) {
                        0 -> SchemeHelper.startKataFloatDialog(context, item.view.surface!!)
                        1 -> openInBrowser(item.view.surface)
                    }
                }
                .show()
    }

    private fun openInBrowser(url: String?) {
        if (url.isNullOrEmpty()) return
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(context, intent, null)
        } catch (_: ActivityNotFoundException) {}
    }

    private fun findItemByPoint(x: Int, y: Int): Item? {
        return mLines
                .map { it.itemList }
                .flatten()
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
        fun onItemClicked(index: Int, selectedByUser: Boolean)
    }

}