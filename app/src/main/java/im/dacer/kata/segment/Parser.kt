package im.dacer.kata.segment

import io.reactivex.Observable

interface Parser<T> {
    fun parse(text: String): Observable<T>
}
