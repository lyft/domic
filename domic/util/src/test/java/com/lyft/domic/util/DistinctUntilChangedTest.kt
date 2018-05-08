package com.lyft.domic.util

import io.reactivex.Observable
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray

class DistinctUntilChangedTest {

    @Test
    fun distinctUntilChangedEmitsDistinctValues() {
        val state = AtomicReferenceArray<Any>(1)

        Observable
                .fromArray("a", "b")
                .distinctUntilChanged(state, 0)
                .test()
                .assertResult("a", "b")
    }

    @Test
    fun distinctUntilChangedDoesNotEmitEqualValues() {
        val state = AtomicReferenceArray<Any>(1)

        Observable
                .fromArray("a", "a")
                .distinctUntilChanged(state, 0)
                .test()
                .assertResult("a")
    }

    @Test
    fun distinctUntilChangedUsesSharedState() {
        val state = AtomicReferenceArray<Any>(1)

        Observable
                .just("a")
                .distinctUntilChanged(state, 0)
                .test()
                .assertResult("a")

        Observable
                .just("a")
                .distinctUntilChanged(state, 0)
                .test()
                .assertResult()

        Observable
                .just("b")
                .distinctUntilChanged(state, 0)
                .test()
                .assertResult("b")
    }
}
