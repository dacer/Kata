package im.dacer.kata.util.engine;

import android.content.Context;

import im.dacer.kata.util.action.BingSearchAction;
import im.dacer.kata.util.action.DuckDuckGoSearchAction;
import im.dacer.kata.util.action.GoogleSearchAction;
import im.dacer.kata.util.action.JishoSearchAction;
import im.dacer.kata.util.action.SearchAction;
import im.dacer.kata.data.local.MultiprocessPref;

public class SearchEngine {

    public static final String GOOGLE = "Google";
    public static final String BING = "Bing";
    public static final String DUCKDUCKGO = "DuckDuckGo";
    public static final String JISHO = "Jisho";

    public static SearchAction getDefaultSearchAction(Context context) {
        MultiprocessPref multiprocessPref = new MultiprocessPref(context);
        return getSearchAction(multiprocessPref.getSearchEngine());
    }

    public static SearchAction getSearchAction(int index) {
        switch (getSupportSearchEngineList()[index]) {
            case GOOGLE:
                return GoogleSearchAction.create();
            case BING:
                return BingSearchAction.create();
            case DUCKDUCKGO:
                return DuckDuckGoSearchAction.create();
            case JISHO:
                return JishoSearchAction.create();
        }
        return null;
    }

    public static SearchAction getSearchAction(String engine) {
        switch (engine) {
            case GOOGLE:
                return GoogleSearchAction.create();
            case BING:
                return BingSearchAction.create();
            case DUCKDUCKGO:
                return DuckDuckGoSearchAction.create();
            case JISHO:
                return JishoSearchAction.create();
        }
        return null;
    }

    public static String[] getSupportSearchEngineList() {
        return new String[]{GOOGLE, BING, DUCKDUCKGO, JISHO};
    }
}
