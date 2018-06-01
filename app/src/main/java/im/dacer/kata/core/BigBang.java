package im.dacer.kata.core;

import java.util.concurrent.Callable;

import im.dacer.kata.segment.SimpleParser;
import im.dacer.kata.segment.parser.KuromojiParser;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class BigBang {

    private static SimpleParser sParser;

    public static boolean initialized() {
        return sParser != null;
    }

    public static Observable<SimpleParser> getSegmentParserAsync() {

        return Observable.fromCallable(new Callable<SimpleParser>() {
            @Override
            public SimpleParser call() throws Exception {
                if (sParser == null) {
                    sParser = new KuromojiParser();
                }
                return sParser;
            }
        }).subscribeOn(Schedulers.io());
    }

    public static void setSegmentParser(SimpleParser parser) {
        sParser = parser;
    }

}
