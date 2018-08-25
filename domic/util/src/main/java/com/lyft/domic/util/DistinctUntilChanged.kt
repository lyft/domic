package com.lyft.domic.util

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.ProtocolViolationException
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.atomic.AtomicReferenceArray

/**
 * Similar to standard [Observable.distinctUntilChanged] but operates on shared atomic state
 * thus letting Domic handle multiple rx streams consistently updating same property.
 *
 * @param sharedState atomic reference array that is used as source of current state when comparison happens.
 * @param stateIndex index of the item in the array that needs to be used to get current state when comparison happens.
 */
fun <T> Observable<T>.sharedDistinctUntilChanged(sharedState: AtomicReferenceArray<Any>, stateIndex: Int): Observable<T> = RxJavaPlugins
        .onAssembly(ObservableSharedDistinctUntilChanged(this, sharedState, stateIndex))

// TODO implement Operator Fusion, looks like required interfaces are internal in RxJava :(
internal class ObservableSharedDistinctUntilChanged<T>(
        private val source: ObservableSource<T>,
        private val sharedState: AtomicReferenceArray<Any>,
        private val stateIndex: Int
) : Observable<T>() {

    override fun subscribeActual(actual: Observer<in T>) {
        source.subscribe(SharedDistinctUntilChangedObserver(actual, sharedState, stateIndex))
    }

    class SharedDistinctUntilChangedObserver<T>(
            private val actual: Observer<in T>,
            private val sharedState: AtomicReferenceArray<Any>,
            private val stateIndex: Int
    ) : Observer<T>, Disposable {

        private var upstream: Disposable? = null
        var done: Boolean = false

        override fun onSubscribe(disposable: Disposable) {
            if (validateDisposable(upstream, disposable)) {
                upstream = disposable
                actual.onSubscribe(this)
            }
        }

        override fun onNext(newValue: T) {
            if (done) {
                return
            }

            val prevValue: Any? = sharedState.get(stateIndex)

            if (newValue != prevValue && sharedState.compareAndSet(stateIndex, prevValue, newValue)) {
                actual.onNext(newValue)
            }
        }

        override fun onComplete() {
            if (done) {
                return
            }

            done = true
            actual.onComplete()
        }

        override fun onError(e: Throwable) {
            if (done) {
                RxJavaPlugins.onError(e)
                return
            }

            done = true
            actual.onError(e)
        }

        override fun isDisposed() = upstream!!.isDisposed

        override fun dispose() = upstream!!.dispose()
    }
}

private fun validateDisposable(current: Disposable?, new: Disposable?): Boolean {
    if (new == null) {
        RxJavaPlugins.onError(NullPointerException("new disposable is null"))
        return false
    }

    if (current != null) {
        new.dispose()
        RxJavaPlugins.onError(ProtocolViolationException("Disposable is already set"))
        return false
    }
    return true
}
