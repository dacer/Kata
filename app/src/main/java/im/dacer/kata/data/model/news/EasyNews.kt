package im.dacer.kata.data.model.news

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import im.dacer.kata.data.NewsDataManager
import java.util.*


@Entity(tableName = "easy_news")
data class EasyNews(@PrimaryKey var news_id: String,
                    var title: String? = null,
                    var news_publication_time: String? = null,
                    var news_prearranged_time: String? = null,
                    var news_web_url: String? = null,
                    var news_web_image_uri: String? = null,
                    var news_web_movie_uri: String? = null,
                    var news_easy_voice_uri: String? = null,
                    var news_easy_movie_uri: String? = null,
                    var content: String? = null,
                    var hasRead: Boolean = false) : NewsItem() {

    override fun updateContent(content: String?) {
        this.content = content
    }

    @Ignore
    override fun news_type(): String {
        return "easy_news"
    }

    @Ignore
    override fun content(): String? {
        return content
    }

    @Ignore
    override fun hasRead(): Boolean {
        return hasRead
    }

    override fun id(): String {
        return news_id
    }

    override fun link(useMirrorSite: Boolean): String? {
        return if (useMirrorSite) {
            "https://www3.nhk.or.jp/news/easy/$news_id/$news_id.html"
        } else {
            "https://${NewsDataManager.NHK_MIRROR_BASE_URL}/news/easy/$news_id/$news_id.html"
        }
    }

    @Ignore
    override fun title(): String? {
        return title
    }

    override fun coverUrl(): String? {
        return news_web_image_uri
    }

    override fun videoUrl(): String? {
        if (news_web_movie_uri.isLegalUrl()) return "https://nhks-vh.akamaihd.net/i/news/$news_web_movie_uri/master.m3u8"
        if (news_easy_movie_uri.isLegalUrl()) return "https://nhks-vh.akamaihd.net/i/news/easy/$news_easy_movie_uri/master.m3u8"
        return null
    }

    override fun voiceUrl(): String? {
        if (!news_easy_voice_uri.isLegalUrl()) return null
        return "https://nhks-vh.akamaihd.net/i/news/easy/$news_easy_voice_uri/master.m3u8"
    }

    override fun timeForParse(): String? {
        return news_prearranged_time
    }

    @Ignore override val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    @Ignore override val DATE_LOCALE = Locale.ENGLISH

    /**
     * check string is something like k10011424371_201805011818_201805011819.mp4 or k10011424371000.mp4
     */
    private fun String?.isLegalUrl(): Boolean {
        if (isNullOrEmpty()) return false
        return this?.contains(".") == true
    }
}
