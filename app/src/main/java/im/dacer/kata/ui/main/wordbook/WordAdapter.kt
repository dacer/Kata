package im.dacer.kata.ui.main.wordbook

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.Word

class WordAdapter : BaseItemDraggableAdapter<Word, BaseViewHolder>(R.layout.item_word, listOf()) {

    override fun convert(helper: BaseViewHolder, item: Word) {
        helper.setText(R.id.baseFormTv, item.baseForm)
    }


}