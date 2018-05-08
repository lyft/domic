package com.lyft.domic.test

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observer

internal class TestPublishRelay<T> private constructor() : Relay<T>() {

    companion object {
        fun <T> create(): TestPublishRelay<T> = TestPublishRelay()
    }

    private val actualRelay = PublishRelay.create<T>()

    private var lastValue: T? = null

    fun lastValue(): T? = lastValue

    override fun accept(value: T) {
        lastValue = value
        actualRelay.accept(value)
    }

    override fun hasObservers(): Boolean = actualRelay.hasObservers()

    override fun subscribeActual(observer: Observer<in T>) {
        actualRelay.subscribeActual(observer)
    }
}
