package im.dacer.kata.ui.main.news

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.squareup.picasso.Picasso
import im.dacer.kata.R
import im.dacer.kata.data.model.news.NewsItem

/**
 * Created by Dacer on 13/02/2018.
 */
class NewsAdapter : BaseQuickAdapter<NewsItem, BaseViewHolder>(R.layout.item_news, listOf()) {

    override fun convert(helper: BaseViewHolder, item: NewsItem) {
        helper.setText(R.id.titleTv, item.title() ?: "")
                .setText(R.id.timeTv, item.time() ?: "")
                .setVisible(R.id.playIcon, !item.videoUrl().isNullOrEmpty())
                .setTextColor(R.id.titleTv, if(item.hasRead()) getColor(R.color.newsSubTitle) else getColor(R.color.newsTitle) )
                .addOnClickListener(R.id.coverImage)
        if (!item.coverUrl().isNullOrEmpty()) {
            Picasso.get()
                    .load(item.coverUrl())
                    .placeholder(ColorDrawable(Color.WHITE))
                    .into(helper.getView<ImageView>(R.id.coverImage))
        } else {
            helper.setImageDrawable(R.id.coverImage, ColorDrawable(Color.WHITE))
        }
    }


    private fun getColor(resId: Int): Int {
        return ContextCompat.getColor(mContext, resId)
    }

}