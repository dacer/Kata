/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package im.dacer.kata.core.action;

import android.net.Uri;

/**
 * Created by baoyongzhang on 2016/10/26.
 */
public class DuckDuckGoSearchAction extends SearchAction {

    public static DuckDuckGoSearchAction create() {
        return new DuckDuckGoSearchAction();
    }

    @Override
    public Uri createSearchUriWithEncodedText(String encodedText) {
        return Uri.parse("https://duckduckgo.com/?q=" + encodedText);
    }
}
