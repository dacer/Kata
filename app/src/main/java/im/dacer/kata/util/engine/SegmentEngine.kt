package im.dacer.kata.util.engine

import android.content.Context
import im.dacer.kata.data.local.MultiprocessPref
import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.util.segment.BigBang
import im.dacer.kata.util.segment.Parser

object SegmentEngine {

    private fun getSegmentParser(context: Context): Parser<List<KanjiResult>> {
        val pref = MultiprocessPref(context)
        return pref.segmentParser
    }

    fun setup(context: Context, force: Boolean = false) {
        if (BigBang.initialized() && !force) return
        val parser = getSegmentParser(context)
        BigBang.setSegmentParser(parser)
    }
}
