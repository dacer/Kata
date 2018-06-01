package im.dacer.kata.util.segment

import io.reactivex.Observable

interface Parser<T> {
    fun parse(text: String): Observable<T>
}
