package com.lyft.domic.util

import io.reactivex.Observable
import java.util.concurrent.atomic.AtomicReferenceArray

/**
 * Similar to distinctUntilChanged but allows to use shared atomic state thus letting Domic handle
 * multiple rx streams updating same property.
 */
fun <T> Observable<out T>.distinctUntilChanged(sharedState: AtomicReferenceArray<Any>, index: Int): Observable<T> = switchMap { newValue ->
    // TODO rewrite as Observable Operator to minimize allocations and implement fusion.
    val prevValue: Any? = sharedState.get(index)

    if (newValue != prevValue && sharedState.compareAndSet(index, prevValue, newValue)) {
        Observable.just(newValue)
    } else {
        Observable.empty()
    }
}
