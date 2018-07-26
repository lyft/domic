package com.lyft.domic.test

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observer
import java.util.concurrent.atomic.AtomicReference

internal class TestPublishRelay<T> private constructor() : Relay<T>() {

    companion object {
        fun <T> create(): TestPublishRelay<T> = TestPublishRelay()
    }

    private val actualRelay = PublishRelay.create<T>()

    private val lastValue = AtomicReference<T?>()

    fun lastValue(): T? = lastValue.get()

    override fun accept(value: T) {
        val previousValue = lastValue.get()

        if (value != previousValue && lastValue.compareAndSet(previousValue, value)) {
            actualRelay.accept(value)
        }
    }

    override fun hasObservers(): Boolean = actualRelay.hasObservers()

    override fun subscribeActual(observer: Observer<in T>) {
        actualRelay.subscribeActual(observer)
    }
}
