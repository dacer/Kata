package im.dacer.kata.data.model.news

import android.content.Context
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*



abstract class NewsItem {
    abstract fun id(): String
    abstract fun title(): String?
    abstract fun coverUrl(): String?
    abstract fun videoUrl(): String?
    abstract fun voiceUrl(): String?
    abstract fun link(): String?
    abstract fun content(): String?
    abstract fun hasRead(): Boolean

    abstract fun updateContent(content: String?)
    abstract fun news_type(): String

    abstract fun timeForParse(): String?
    abstract val DATE_FORMAT : String
    abstract val DATE_LOCALE : Locale


    fun date(): Date? {
        var result: Date? = null
        try {
            val df = SimpleDateFormat(DATE_FORMAT, DATE_LOCALE)
            df.timeZone = TimeZone.getTimeZone("GMT+9:00")
            val date = df.parse(timeForParse())
            result = date
        } catch (_: Exception) {
        }
        return result
    }

    fun timeInMillis(): Long {
        date()?.run {
            return this.time
        }
        return 0
    }

    fun timeForDisplay(context: Context): String {
        date()?.run {
            return DateUtils.formatDateTime(context,
                    this.time,
                    DateUtils.FORMAT_ABBREV_ALL or
                            DateUtils.FORMAT_SHOW_DATE or
                            DateUtils.FORMAT_SHOW_TIME or
                            DateUtils.FORMAT_SHOW_WEEKDAY)
        }
        return ""
    }
}
