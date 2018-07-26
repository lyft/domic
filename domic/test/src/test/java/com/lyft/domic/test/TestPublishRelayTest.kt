package com.lyft.domic.test

import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TestPublishRelayTest {

    private val relay = TestPublishRelay.create<String>()

    @Test
    fun hasNoObserversByDefault() {
        assertThat(relay.hasObservers()).isFalse()
    }

    @Test
    fun hasObserver() {
        relay.subscribe(TestObserver())
        assertThat(relay.hasObservers()).isTrue()
    }

    @Test
    fun noValuesByDefault() {
        val observer = relay.test()
        observer.assertNoValues()
    }

    @Test
    fun notTerminatedByDefault() {
        val observer = relay.test()
        observer.assertNotTerminated()
    }

    @Test
    fun acceptEmitsUniqueValue() {
        val observer = relay.test()

        relay.accept("a")
        observer.assertValue("a")

        relay.accept("b")
        observer.assertValues("a", "b")
    }

    @Test
    fun acceptSkipsEqualValues() {
        val observer = relay.test()

        relay.accept("a")
        observer.assertValue("a")

        relay.accept("a")
        observer.assertValueCount(1)
    }
}
