package com.lyft.domic.util

import io.reactivex.Observable
import org.junit.Test
import java.util.concurrent.atomic.AtomicReferenceArray

class DistinctUntilChangedTest {

    @Test
    fun distinctUntilChangedEmitsDistinctValues() {
        val state = AtomicReferenceArray<Any>(1)

        Observable
                .fromArray("a", "b")
                .sharedDistinctUntilChanged(state, 0)
                .test()
                .assertResult("a", "b")
    }

    @Test
    fun distinctUntilChangedDoesNotEmitEqualValues() {
        val state = AtomicReferenceArray<Any>(1)

        Observable
                .fromArray("a", "a")
                .sharedDistinctUntilChanged(state, 0)
                .test()
                .assertResult("a")
    }

    @Test
    fun distinctUntilChangedUsesSharedState() {
        val state = AtomicReferenceArray<Any>(1)

        Observable
                .just("a")
                .sharedDistinctUntilChanged(state, 0)
                .test()
                .assertResult("a")

        Observable
                .just("a")
                .sharedDistinctUntilChanged(state, 0)
                .test()
                .assertResult()

        Observable
                .just("b")
                .sharedDistinctUntilChanged(state, 0)
                .test()
                .assertResult("b")
    }
}
