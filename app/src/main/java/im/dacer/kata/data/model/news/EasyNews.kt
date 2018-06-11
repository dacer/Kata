package im.dacer.kata.data.model.news

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "easy_news")
data class EasyNews(@PrimaryKey var news_id: String,
                    var title: String? = null,
                    var news_publication_time: String? = null,
                    var news_web_url: String? = null,
                    var news_web_image_uri: String? = null,
                    var news_web_movie_uri: String? = null,
                    var news_easy_voice_uri: String? = null,
                    var news_easy_movie_uri: String? = null,
                    var content: String? = null,
                    var hasRead: Boolean = false) : NewsItem {

    override fun link(): String? {
        return news_web_url
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

    override fun time(): String? {
        return news_publication_time?.substring(5)
    }

    /**
     * check string is something like k10011424371_201805011818_201805011819.mp4 or k10011424371000.mp4
     */
    private fun String?.isLegalUrl(): Boolean {
        if (isNullOrEmpty()) return false
        return this?.contains(".") == true
    }
    /**
     * a simple parser for m3u8 file
     */
//    private fun getNextLineBy(condition: (String) -> Boolean): Single<String> {
//        return Rx2AndroidNetworking.get(MOVIE_VOICE_URL)
//                .build()
//                .stringSingle
//                .map {
//                    var returnNextLine = false
//                    for (line in it.split("\n")) {
//                        if (returnNextLine) return@map line
//                        Timber.e("$line --> ${condition(line)}")
//                        if (condition(line)) {
//                            returnNextLine = true
//                        }
//                    }
//                    return@map ""
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
}
