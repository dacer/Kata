/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package im.dacer.kata.util.engine;

import im.dacer.kata.util.segment.BigBang;
import im.dacer.kata.util.segment.Parser;
import im.dacer.kata.util.segment.parser.KuromojiParser;

/**
 * Created by baoyongzhang on 2016/10/28.
 */
public class SegmentEngine {

    private static Parser getSegmentParser() {
        return new KuromojiParser();
    }

    public static void setup() {
        if (BigBang.initialized()) return;
        Parser parser = SegmentEngine.getSegmentParser();
        BigBang.setSegmentParser(parser);
    }
}
