package im.dacer.kata.ui.main.wordbook

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.Word
import im.dacer.kata.data.model.bigbang.WordWithMeaning
import im.dacer.kata.data.room.dao.WordDao

class WordAdapter : BaseItemDraggableAdapter<WordWithMeaning, BaseViewHolder>(R.layout.item_word, mutableListOf()) {

    override fun convert(helper: BaseViewHolder, item: WordWithMeaning) {
        helper.setText(R.id.baseFormTv, item.word.baseForm)
        helper.setText(R.id.explanationTv, item.meaning)
    }

}