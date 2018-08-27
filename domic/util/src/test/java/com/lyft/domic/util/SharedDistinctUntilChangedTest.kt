package com.lyft.domic.util

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import java.lang.Exception
import java.util.concurrent.atomic.AtomicReferenceArray

class SharedDistinctUntilChangedTest {

    @Test
    fun sharedDistinctUntilChangedEmitsDistinctValues() {
        val state = AtomicReferenceArray<Any>(1)

        Observable
                .fromArray("a", "b")
                .sharedDistinctUntilChanged(state, 0)
                .test()
                .assertResult("a", "b")
    }

    @Test
    fun sharedDistinctUntilChangedDoesNotEmitEqualValues() {
        val state = AtomicReferenceArray<Any>(1)

        Observable
                .fromArray("a", "a")
                .sharedDistinctUntilChanged(state, 0)
                .test()
                .assertResult("a")
    }

    @Test
    fun sharedDistinctUntilChangedUsesSharedState() {
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

    @Test
    fun sharedDistinctUntilChangedPropagatesOnComplete() {
        val upstream = PublishSubject.create<Unit>()
        val state = AtomicReferenceArray<Any>(1)

        val testObserver = upstream
                .sharedDistinctUntilChanged(state, 0)
                .test()

        testObserver.assertNotTerminated()

        upstream.onComplete()
        testObserver.assertResult()
    }

    @Test
    fun sharedDistinctUntilChangedPropagatesOnError() {
        val upstream = PublishSubject.create<Unit>()
        val state = AtomicReferenceArray<Any>(1)

        val testObserver = upstream
                .sharedDistinctUntilChanged(state, 0)
                .test()

        testObserver.assertNotTerminated()

        val error = Exception()
        upstream.onError(error)

        testObserver.assertError(error)
        testObserver.assertNoValues()
        testObserver.assertNotComplete()
    }
}
