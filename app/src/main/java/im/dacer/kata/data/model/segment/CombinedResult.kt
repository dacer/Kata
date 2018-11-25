package im.dacer.kata.data.model.segment

import android.content.Context
import im.dacer.kata.R

data class CombinedResult(val strForSearch: String, var meaningStr: String,
                          val readingStr: String, val contextStr: CharSequence?) {
    fun getMeaning(context: Context):String {
        return if (meaningStr.isBlank()) {
            context.getString(R.string.not_found_error, strForSearch)
        } else { meaningStr }
    }
}
