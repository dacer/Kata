package im.dacer.kata.data

import com.google.gson.Gson
import com.rx2androidnetworking.Rx2AndroidNetworking
import im.dacer.kata.data.model.news.EasyNews
import im.dacer.kata.data.model.news.NhkNews
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsDataManager @Inject constructor() {

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


    /**
     * page >= 1
     */
    fun getNhkNews(page: Int): Observable<ArrayList<NhkNews>> {
        val gson = Gson()
        return Rx2AndroidNetworking.get(getNhkNewsUrl(page))
                .build()
                .jsonObjectObservable
                .map { obj -> obj["channel"] as JSONObject }
                .map { jsonObject: JSONObject ->
                    val result: ArrayList<NhkNews> = arrayListOf()
                    val itemList = jsonObject.getJSONArray("item")
                    var i = 0

                    while (i < itemList.length()) {
                        result.add(jsonArrayToNhkNews(itemList[i] as JSONObject, gson))
                        i++
                    }

                    return@map result
                }
    }

    private fun jsonArrayToEasyNews(jsonArray: JSONArray, gson: Gson): List<EasyNews> {
        return gson.fromJson(jsonArray.toString(), Array<EasyNews>::class.java).asList()
    }

    private fun jsonArrayToNhkNews(jsonObject: JSONObject, gson: Gson): NhkNews {
        return gson.fromJson(jsonObject.toString(), NhkNews::class.java)
    }

    companion object {
        private const val NHK_EASY_NEWS_URL = "https://www3.nhk.or.jp/news/easy/news-list.json"
        private fun getNhkNewsUrl(page: Int) = "https://www3.nhk.or.jp/news/json16/new_${String.format("%03d", page)}.json"
    }

}