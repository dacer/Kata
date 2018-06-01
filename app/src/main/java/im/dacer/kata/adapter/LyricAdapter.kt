package im.dacer.kata.adapter

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import im.dacer.kata.R
import im.dacer.kata.core.model.Music

/**
 * Created by Dacer on 13/02/2018.
 */
class LyricAdapter : BaseItemDraggableAdapter<im.dacer.kata.core.model.Music, BaseViewHolder>(R.layout.item_song, listOf()) {

    override fun convert(helper: BaseViewHolder, item: im.dacer.kata.core.model.Music) {
        helper.setText(R.id.titleTv, item.name)
                .setText(R.id.artistTV, "${getArtistsName(item)} (${item.album.name})")
    }

    private fun getArtistsName(item: im.dacer.kata.core.model.Music): String = item.artists.joinToString(", ") { it.name }

}