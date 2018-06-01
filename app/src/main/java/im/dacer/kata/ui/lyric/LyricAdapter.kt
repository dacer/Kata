package im.dacer.kata.ui.lyric

import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import im.dacer.kata.R
import im.dacer.kata.data.model.bigbang.Music

/**
 * Created by Dacer on 13/02/2018.
 */
class LyricAdapter : BaseItemDraggableAdapter<Music, BaseViewHolder>(R.layout.item_song, listOf()) {

    override fun convert(helper: BaseViewHolder, item: Music) {
        helper.setText(R.id.titleTv, item.name)
                .setText(R.id.artistTV, "${getArtistsName(item)} (${item.album.name})")
    }

    private fun getArtistsName(item: Music): String = item.artists.joinToString(", ") { it.name }

}