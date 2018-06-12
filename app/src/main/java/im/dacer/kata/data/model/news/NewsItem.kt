package im.dacer.kata.data.model.news

interface NewsItem {
    fun id(): String
    fun title(): String?
    fun coverUrl(): String?
    fun videoUrl(): String?
    fun voiceUrl(): String?
    fun link(): String?
    fun time(): String?
    fun content(): String?
    fun hasRead(): Boolean

    fun news_type(): String
}
