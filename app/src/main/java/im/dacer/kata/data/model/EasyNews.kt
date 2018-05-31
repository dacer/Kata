package im.dacer.kata.data.model

import android.text.TextUtils

import org.immutables.value.Value

import io.reactivex.Observable
import io.reactivex.Single

data class EasyNews(private val news_id: String? = null,
                    private val news_priority_number: String? = null,
                    private val title: String? = null,
                    private val news_publication_time: String? = null,
                    private val news_web_url: String? = null,
                    private val news_web_image_uri: String? = null,
                    private val news_web_movie_uri: String? = null,
                    private val news_easy_voice_uri: String? = null) : NewsItem {


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
        return news_publication_time
    }
}
