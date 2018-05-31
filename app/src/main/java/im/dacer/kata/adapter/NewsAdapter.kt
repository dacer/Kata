package im.dacer.kata.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseItemDraggableAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import im.dacer.kata.R
import im.dacer.kata.core.model.History
import im.dacer.kata.data.model.NewsItem

/**
 * Created by Dacer on 13/02/2018.
 */
class NewsAdapter : BaseItemDraggableAdapter<NewsItem, BaseViewHolder>(R.layout.item_news, listOf()) {

    override fun convert(helper: BaseViewHolder, item: NewsItem) {
        helper.setText(R.id.titleTv, item.title())
                .setText(R.id.timeTv, item.time())
        if (!item.coverUrl().isNullOrEmpty()) {
            Picasso.get()
                    .load(item.coverUrl())
                    .placeholder(ColorDrawable(Color.WHITE))
                    .into(helper.getView<ImageView>(R.id.coverImage))
        } else {
            helper.setImageDrawable(R.id.coverImage, ColorDrawable(Color.WHITE))
        }

    }


}