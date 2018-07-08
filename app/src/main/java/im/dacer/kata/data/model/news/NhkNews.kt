package im.dacer.kata.data.model.news

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.util.*


@Entity(tableName = "nhk_news")
data class NhkNews(
        @PrimaryKey var id: String,
        var title: String? = null,
        var pubDate: String? = null,
        var cate: String? = null,
        var link: String? = null,
        var imgPath: String? = null,
        var iconPath: String? = null,
        var videoPath: String? = null,

        //↓ need generate
        var content: String? = null,
        var hasRead: Boolean = false) : NewsItem() {


    override fun updateContent(content: String?) {
        this.content = content
    }

    @Ignore
    override fun news_type(): String = "nhk_news"

    @Ignore
    override fun content(): String? = content

    @Ignore
    override fun hasRead(): Boolean = hasRead

    @Ignore
    override fun id(): String = id

    @Ignore
    override fun link(): String? {
        return link.pathToUrl()
    }

    @Ignore
    override fun title(): String? = title

    override fun coverUrl(): String? {
        return imgPath.pathToUrl() ?: iconPath.pathToUrl()
    }

    override fun videoUrl(): String? {
        return videoPath.pathToUrl()
    }

    override fun voiceUrl(): String? = null

    override fun timeForParse(): String? {
        return pubDate?.replace(" +0900", "")
    }

    @Ignore override val DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss"
    @Ignore override val DATE_LOCALE = Locale.ENGLISH

    private fun String?.pathToUrl(): String? {
        if (this == null) return null
        if (startsWith("//")) return "https:$this"
        if (startsWith("html")) return "https://www3.nhk.or.jp/news/$this"
        return null
    }
}
