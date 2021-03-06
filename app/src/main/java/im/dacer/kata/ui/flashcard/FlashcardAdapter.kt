package im.dacer.kata.ui.flashcard

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import im.dacer.kata.R
import im.dacer.kata.data.local.SearchDictHelper
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.room.dao.ContextStrDao
import im.dacer.kata.util.LangUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FlashcardAdapter(context: Context, private val searchDictHelper: SearchDictHelper,
                       private val langUtils: LangUtils, private val contextStrDao: ContextStrDao) :
        ArrayAdapter<Word>(context, 0) {

    private var dictDisposable: Disposable? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        var contentView = convertView

        if (contentView == null) {
            val inflater = LayoutInflater.from(context)
            contentView = inflater.inflate(R.layout.item_flash_card, parent, false)
            holder = ViewHolder(contentView)
            contentView.tag = holder
        } else {
            holder = contentView.tag as ViewHolder
        }

        val word = getItem(position)

        holder.baseFormTv.text = word!!.baseForm
        holder.queryTimesTv.text = word.queryTimes.toString()
        val color = Color.parseColor(MATERIAL_COLORS[position % MATERIAL_COLORS.size])
        holder.baseFormTv.text = holder.baseFormTv.text
        holder.backgroundLayout.setCardBackgroundColor(color)
        showContext(holder, word)

        holder.bottomTv.setOnClickListener {
            if (holder.pronunciationText.visibility == View.VISIBLE) {
                showContext(holder, word)
            } else {
                showMeaning(holder, word)
            }
        }
        return contentView!!
    }

    private fun showMeaning(holder: ViewHolder, word: Word) {
        holder.bottomTv.text = context.getText(R.string.see_usage)
        dictDisposable?.dispose()
        dictDisposable = searchDictHelper.searchForCombineResultAndTranslateIfNoMeaning(word.baseForm, langUtils)
                .subscribe({
                    val meaningStr = it.meaningStr
                    holder.contextTv.text = if (meaningStr.isBlank()) {
                        context.getString(R.string.not_found_error, word.baseForm)
                    } else { meaningStr }

                    val readingStr = it.readingStr
                    holder.pronunciationText.text = if (readingStr.isBlank()) { word.baseForm } else { readingStr }
                    holder.pronunciationText.visibility = View.VISIBLE
                }, { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() })
    }

    private fun showContext(holder: ViewHolder, word: Word) {
        holder.bottomTv.text = context.getText(R.string.see_definition)
        searchDictHelper.getContextStr(word, contextStrDao)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { holder.contextTv.text = it }
        holder.pronunciationText.visibility = View.GONE
    }

    private class ViewHolder(view: View) {
        var baseFormTv: TextView = view.findViewById(R.id.baseFormTv)
        var contextTv: TextView = view.findViewById(R.id.contextTv)
        var queryTimesTv: TextView = view.findViewById(R.id.queryTimesTv)
        var pronunciationText: TextView = view.findViewById(R.id.pronunciationText)
        var bottomTv: TextView = view.findViewById(R.id.bottomTv)
        var backgroundLayout: CardView = view.findViewById(R.id.backgroundLayout)
    }

    companion object {
        const val CIRCLE_SYMBOL = "\u25CB "
        private val MATERIAL_COLORS = listOf(
                "#C62828",
                "#AD1457",
                "#6A1B9A",
                "#4527A0",
                "#283593",
                "#1565C0",
                "#0277BD",
                "#00838F",
                "#00695C",
                "#2E7D32",
                "#F9A825",
                "#FF8F00",
                "#EF6C00",
                "#D84315").shuffled()
    }
}
