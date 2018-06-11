package im.dacer.kata.data.model.news

interface NewsItem {
    fun title(): String?
    fun coverUrl(): String?
    fun videoUrl(): String?
    fun voiceUrl(): String?
    fun link(): String?
    fun time(): String?
}
