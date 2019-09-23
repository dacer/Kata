package im.dacer.kata.util.segment;

import java.util.List;

import im.dacer.kata.data.model.segment.KanjiResult;
import im.dacer.kata.util.segment.parser.ApiParser;
import io.reactivex.Observable;

public class BigBang {

    private static Parser<List<? extends KanjiResult>> sParser;

    public static boolean initialized() {
        return sParser != null;
    }

    public static Observable<List<? extends KanjiResult>> parse(String text) {
        if (sParser == null) {
            sParser = new ApiParser();
        }
        return sParser.parse(text);
    }

    public static void setSegmentParser(Parser<List<? extends KanjiResult>> parser) {
        sParser = parser;
    }

}
