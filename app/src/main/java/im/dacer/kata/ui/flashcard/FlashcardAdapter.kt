package im.dacer.kata.ui.flashcard

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.Word


class FlashcardAdapter(context: Context) : ArrayAdapter<Word>(context, 0) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
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

        holder.baseFormTv.text = word.baseForm
        holder.contextTv.text = word.contextText
        holder.queryTimesTv.text = word.queryTimes.toString()
        val color = Color.parseColor(MATERIAL_COLORS[position % MATERIAL_COLORS.size])
        //todo delete it
        holder.baseFormTv.text = "${holder.baseFormTv.text} ${MATERIAL_COLORS[position % MATERIAL_COLORS.size]}"
        holder.backgroundLayout.setCardBackgroundColor(color)

        return contentView!!
    }

    private class ViewHolder(view: View) {
        var baseFormTv: TextView = view.findViewById(R.id.baseFormTv)
        var contextTv: TextView = view.findViewById(R.id.contextTv)
        var queryTimesTv: TextView = view.findViewById(R.id.queryTimesTv)
        var backgroundLayout: CardView = view.findViewById(R.id.backgroundLayout)
    }

    companion object {
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
                "#D84315",
                "#4E342E",
                "#37474F").shuffled()
    }
}
