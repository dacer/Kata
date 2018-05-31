package im.dacer.kata.data.model

import io.reactivex.Single

interface NewsItem {
    fun title(): String?
    fun coverUrl(): String?
    fun videoUrl(): Single<String>
    fun voiceUrl(): Single<String>
    fun time(): String?
}
