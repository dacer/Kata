package im.dacer.kata.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rx2androidnetworking.Rx2AndroidNetworking
import im.dacer.kata.core.model.MusicSearchResult
import im.dacer.kata.data.model.EasyNews
import im.dacer.kata.data.model.NewsItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject

object NewsDataManager {
    private val NHK_EASY_NEWS_URL = "http://www3.nhk.or.jp/news/easy/news-list.json"

    fun getEasyNews(): Observable<ArrayList<EasyNews>> {
        val gson = Gson()
        return Rx2AndroidNetworking.get(NHK_EASY_NEWS_URL)
                .build()
                .jsonArrayObservable
                .map { array -> array[0] as JSONObject }
                .map { jsonObject: JSONObject ->
                    val result: ArrayList<EasyNews> = arrayListOf()
                    jsonObject.keys().forEach { result.addAll(jsonArrayToEasyNews(jsonObject[it] as JSONArray, gson)) }
                    return@map result
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    private fun jsonArrayToEasyNews(jsonArray: JSONArray, gson: Gson): List<EasyNews> {
        return gson.fromJson(jsonArray.toString(), Array<EasyNews>::class.java).asList()
    }

}