package im.dacer.kata.util.segment;

import java.util.concurrent.Callable;

import im.dacer.kata.util.segment.parser.KuromojiParser;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class BigBang {

    private static SimpleParser sParser;

    public static boolean initialized() {
        return sParser != null;
    }

    public static Observable<SimpleParser> getSegmentParserAsync() {

        return Observable.fromCallable(() -> {
            if (sParser == null) {
                sParser = new KuromojiParser();
            }
            return sParser;
        }).subscribeOn(Schedulers.io());
    }

    public static void setSegmentParser(SimpleParser parser) {
        sParser = parser;
    }

}
