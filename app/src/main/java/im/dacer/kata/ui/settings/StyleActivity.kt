package im.dacer.kata.ui.settings

import android.os.Bundle
import android.view.MenuItem
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import im.dacer.kata.R
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.model.bigbang.BigBangStyle
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.ui.base.BaseTransparentSwipeActivity
import im.dacer.kata.util.extension.applyHeight
import im.dacer.kata.util.extension.getNavBarHeight
import im.dacer.kata.util.extension.setMyActionBar
import kotlinx.android.synthetic.main.activity_style.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.jetbrains.anko.backgroundColor
import javax.inject.Inject

class StyleActivity : BaseTransparentSwipeActivity(), ColorPickerDialogListener{
    @Inject lateinit var appPref: MultiprocessPref

    override fun layoutId() = R.layout.activity_style

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent().inject(this)
        setMyActionBar(myToolbar)
        bottomPadding.applyHeight(getNavBarHeight())
        kataLayout.setKanjiResultData(EXAMPLE_KANJI_RESULT_LIST)

        textSizeSeekBar.setOnProgressChangeListener(object : SimpleListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                kataLayout.itemTextSize = value.toFloat()
            }
        })
        furiganaTextSizeSeekBar.setOnProgressChangeListener(object : SimpleListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                kataLayout.itemFuriganaTextSize = value.toFloat()
            }
        })

        lineSpaceSeekBar.setOnProgressChangeListener(object : SimpleListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                kataLayout.lineSpace = value
            }
        })

        itemSpace.setOnProgressChangeListener(object : SimpleListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
                kataLayout.itemSpace = value
            }
        })
        backgroundColorBtn.setOnClickListener {
            val colorPicker = ColorPickerDialog.newBuilder().setColor(appPref.backgroundColor).create()
            colorPicker.setColorPickerDialogListener(this)
            colorPicker.show(fragmentManager, "COLOR_PICKER")
        }

        textSizeSeekBar.progress = appPref.getItemTextSize()
        furiganaTextSizeSeekBar.progress = appPref.getFuriganaItemTextSize()
        lineSpaceSeekBar.progress = appPref.getLineSpace()
        itemSpace.progress = appPref.getItemSpace()
        kataLayout.backgroundColor = appPref.backgroundColor

    }

    override fun onPause() {
        super.onPause()
        updatePref()
    }

    override fun onDialogDismissed(dialogId: Int) {
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        kataLayout.backgroundColor = color
        appPref.backgroundColor = color
    }

    private fun updatePref() {
        appPref.bigBangStyle = BigBangStyle(
                itemSpace.progress,
                lineSpaceSeekBar.progress,
                textSizeSeekBar.progress,
                furiganaTextSizeSeekBar.progress
        )
    }

    internal abstract class SimpleListener : DiscreteSeekBar.OnProgressChangeListener {

        override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {}

        override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {}

        override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {}
    }

    companion object {
        val EXAMPLE_KANJI_RESULT_LIST = listOf(
                KanjiResult("日本国", furigana = "にほんこく"),
                KanjiResult("または"),
                KanjiResult("日本", furigana = "にほん"),
                KanjiResult("は"),
                KanjiResult("、"),
                KanjiResult("東アジア", furigana = "ひがしあじあ"),
                KanjiResult("に"),
                KanjiResult("位置", furigana = "いち"),
                KanjiResult("する"),
                KanjiResult("日本列島", furigana = "にほんれっとう"),
                KanjiResult("及び", furigana = "および"),
                KanjiResult("、"),
                KanjiResult("南西諸島", furigana = "なんせいしょとう"),
                KanjiResult("・"),
                KanjiResult("伊豆諸島", furigana = "いずしょとう"),
                KanjiResult("・"),
                KanjiResult("小笠原諸島", furigana = "おがさわらしょとう"),
                KanjiResult("など"),
                KanjiResult("から"),
                KanjiResult("成る", furigana = "なる"),
                KanjiResult("島国", furigana = "しまぐに"),
                KanjiResult("である"))

    }
}
