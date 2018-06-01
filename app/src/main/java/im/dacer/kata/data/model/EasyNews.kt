package im.dacer.kata.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.text.TextUtils

import org.immutables.value.Value

import io.reactivex.Observable
import io.reactivex.Single

@Entity(tableName = "easy_news")
data class EasyNews(@PrimaryKey var news_id: String,
                    var title: String? = null,
                    var news_publication_time: String? = null,
                    var news_web_url: String? = null,
                    var news_web_image_uri: String? = null,
                    var news_web_movie_uri: String? = null,
                    var news_easy_voice_uri: String? = null,
                    var content: String? = null) : NewsItem {

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

    override fun videoUrl(): Single<String> {
        return if (TextUtils.isEmpty(news_id)) Single.just("") else Single.just("")
    }

    override fun voiceUrl(): Single<String> {
        return if (TextUtils.isEmpty(news_id)) Single.just("") else Single.just("https://nhks-vh.akamaihd.net/i/news/easy/$news_id.mp4/master.m3u8")
    }

    override fun time(): String? {
        return news_publication_time?.substring(5)
    }
}
