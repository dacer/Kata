package im.dacer.kata.util.segment

import im.dacer.kata.data.model.segment.KanjiResult
import im.dacer.kata.util.segment.parser.KuromojiParser
import io.reactivex.Observable

object BigBang {

    private var sParser: Parser<List<KanjiResult>>? = null

    fun initialized(): Boolean {
        return sParser != null
    }

    fun parse(text: String): Observable<List<KanjiResult>> {
        if (sParser == null) {
            sParser = KuromojiParser()
        }
        return sParser!!.parse(text)
    }

    fun setSegmentParser(parser: Parser<List<KanjiResult>>) {
        sParser = parser
    }

}
