package im.dacer.kata.ui.main.inbox

import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.History

/**
 * Created by Dacer on 13/02/2018.
 */
class HistoryAdapter : BaseItemDraggableAdapter<History, BaseViewHolder>(R.layout.item_history, listOf()) {

    override fun convert(helper: BaseViewHolder, item: History) {
        helper.setText(R.id.historyTv, item.text)
                .setText(R.id.aliasTv, item.alias)
                .setVisible(R.id.starIcon, item.star == true)

        if ((item.star == true) && item.alias.isNullOrEmpty()) {
            helper.setText(R.id.aliasTv, R.string.no_alias)
        }
        helper.getView<TextView>(R.id.aliasTv).visibility =
                if(helper.getView<TextView>(R.id.aliasTv).text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }


}