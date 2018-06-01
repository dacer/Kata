package im.dacer.kata.segment

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

abstract class AsyncParser<T> : Parser<T> {

    override fun parse(text: String): Observable<T> {
        return Observable.fromCallable { parseSync(text) }.subscribeOn(Schedulers.io())
    }

    @Throws(SegmentException::class)
    open fun parseSync(text: String): T {
        throw SegmentException("Not yet implemented")
    }
}
