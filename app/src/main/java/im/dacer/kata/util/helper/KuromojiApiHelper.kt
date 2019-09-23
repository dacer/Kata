package im.dacer.kata.util.helper

import com.rx2androidnetworking.Rx2AndroidNetworking
import im.dacer.kata.data.model.bigbang.KuromojiApiResult
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

/**
 * Created by Dacer on 21/09/2019.
 */
object KuromojiApiHelper {
    private const val BASE_URL = "http://kuromoji.dacer.im:9696"

    fun search(text: String): Observable<KuromojiApiResult> {
        val jsonObject = JSONObject()
        jsonObject.put("body", text)
        return Rx2AndroidNetworking.post(BASE_URL)
                .addJSONObjectBody(jsonObject)
                .build()
                .getObjectObservable(KuromojiApiResult::class.java)
                .subscribeOn(Schedulers.io())
    }

}