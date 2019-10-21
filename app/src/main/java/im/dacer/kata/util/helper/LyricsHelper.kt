package im.dacer.kata.util.helper

import com.rx2androidnetworking.Rx2AndroidNetworking
import im.dacer.kata.data.model.bigbang.MusicSearchResult
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

/**
 * Created by Dacer on 26/02/2018.
 */
object LyricsHelper {
    private val BASE_URL = "https://music.dacer.im"
    private val SEARCH_URL = "$BASE_URL/search"
    private val LYRIC_URL = "$BASE_URL/lyric"
    private val MUSIC_URL = "$BASE_URL/music/url"
    private const val SEARCH_LIMIT = 30


    /**
     * page from 1
     */
    fun search(keywords: String, page: Int = 1): Observable<MusicSearchResult.Result> {
        return Rx2AndroidNetworking.get(SEARCH_URL)
                .addQueryParameter("type", "1")
                .addQueryParameter("keywords", keywords)
                .addQueryParameter("limit", SEARCH_LIMIT.toString())
                .addQueryParameter("offset", (SEARCH_LIMIT * (page - 1)).toString())
                .build()
                .getObjectObservable(MusicSearchResult::class.java)
                .map { return@map it.result }
                .subscribeOn(Schedulers.io())
    }

    fun getLyric(id: Long): Observable<String> {
        return Rx2AndroidNetworking.get(LYRIC_URL)
                .addQueryParameter("id", id.toString())
                .build()
                .jsonObjectObservable
                .map { return@map it.getJSONObject("lrc")
                        .getString("lyric")
                        .replace(Regex("^\\[\\d\\d:\\d\\d.*?]", RegexOption.MULTILINE), "")}
                .map {
                    it.split("\n").filter {
                        !(it.matches(Regex("^\\[(by|ti|ar|al).+]")) ||
                                it.matches(Regex("^.*作曲.*[:：].+")) ||
                                it.matches(Regex("^.*(作词|作詞).*[:：].+")))
                    }.joinToString("\n")
                }
                .map { it.replace(Regex("^\n+"), "") }
                .subscribeOn(Schedulers.io())
    }

    fun getMusicUrl(id: Long): Observable<String> {
        return Rx2AndroidNetworking.get(MUSIC_URL)
                .addQueryParameter("id", id.toString())
                .build()
                .jsonObjectObservable
                .map {
                    return@map (it.getJSONArray("data").get(0) as JSONObject).getString("url")
                }
                .onErrorReturn { "" }
                .subscribeOn(Schedulers.io())
    }
}