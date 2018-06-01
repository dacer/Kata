/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package im.dacer.kata.core.action;

import android.net.Uri;

/**
 * Created by baoyongzhang on 2016/10/26.
 */
public class BingSearchAction extends SearchAction {

    public static BingSearchAction create() {
        return new BingSearchAction();
    }

    @Override
    public Uri createSearchUriWithEncodedText(String encodedText) {
        return Uri.parse("https://www.bing.com/search?q=" + encodedText);
    }
}
