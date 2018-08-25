package com.lyft.domic.android.rendering

import com.lyft.domic.api.rendering.Change
import com.lyft.domic.api.rendering.Renderer
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicBoolean

class AndroidRendererTest {

    private val choreographer = TestChoreographer()
    private val timeScheduler = TestScheduler()
    private val mainThreadChecker = mock<Callable<Boolean>>()
    private val renderer: Renderer = AndroidRenderer(
            choreographer.choreographer,
            timeScheduler,
            RenderingBufferImpl(),
            mainThreadChecker
    )
    private val changes = listOf<Change>(mock(), mock(), mock(), mock())

    @Test
    fun postsToChoreographerByDefault() {
        assertThat(choreographer.callbacksCount()).isEqualTo(1)
    }

    @Test
    fun postsItselfToChoreographerAgain() {
        choreographer.simulateCallbacksCall()
        assertThat(choreographer.callbacksCount()).isEqualTo(1)
    }

    @Test
    fun rendersChangesOnlyWhenChoreographerSignals() {
        renderer.render(Observable.fromIterable(changes))
        changes.forEach { verifyZeroInteractions(it) }

        choreographer.simulateCallbacksCall()
        changes.forEach { change -> verify(change).perform() }
    }

    @Test
    fun rendersChangesPostedAsIndividualObservablesOnlyWhenChoreographerSignals() {
        changes.forEach { change -> renderer.render(Observable.just(change)) }
        changes.forEach { verifyZeroInteractions(it) }

        choreographer.simulateCallbacksCall()
        changes.forEach { change -> verify(change).perform() }
    }

    @Test
    fun changePostedAfterChoreographerSignalWaitsForNextSignal() {
        val change1 = changes[0]
        val change2 = changes[1]

        val changes = PublishSubject.create<Change>()

        renderer.render(changes)

        changes.onNext(change1)
        verifyZeroInteractions(change1)
        choreographer.simulateCallbacksCall()
        verify(change1).perform()

        changes.onNext(change2)
        verifyZeroInteractions(change2)
        choreographer.simulateCallbacksCall()
        verify(change2).perform()
    }

    @Test
    fun doesNotPerformOldChangesAgain() {
        val change1 = changes[0]
        val change2 = changes[1]

        val changes = PublishSubject.create<Change>()

        renderer.render(changes)

        changes.onNext(change1)
        verifyZeroInteractions(change1)
        choreographer.simulateCallbacksCall()
        verify(change1).perform()

        changes.onNext(change2)
        verifyZeroInteractions(change2)
        choreographer.simulateCallbacksCall()
        verify(change2).perform()

        verifyNoMoreInteractions(change1, change2)
    }

    @Test
    fun equalChangesInSameObservablesAreDebouncedWithinChoreographerSignals() {
        val change1 = TestChange(id = "a")
        val change2 = TestChange(id = "a")

        renderer.render(Observable.fromIterable(listOf(change1, change2)))

        choreographer.simulateCallbacksCall()

        assertThat(change1.performed).isFalse()
        assertThat(change2.performed).isTrue()
    }

    @Test
    fun equalChangesInDifferentObservablesAreDebouncedWithinBufferTimeWindow() {
        val change1 = TestChange("a")
        val change2 = TestChange("a")

        renderer.render(Observable.just(change1))
        renderer.render(Observable.just(change2))

        choreographer.simulateCallbacksCall()

        assertThat(change1.performed).isFalse()
        assertThat(change2.performed).isTrue()
    }

    @Test
    fun disposingRenderingCallRemovesChangesFromBuffer() {
        val disposable = renderer.render(Observable.fromIterable(changes))

        disposable.dispose()

        choreographer.simulateCallbacksCall()

        changes.forEach { verifyZeroInteractions(it) }
    }

    @Test
    fun renderCurrentBufferThrowsOnWrongThread() {
        renderer.render(Observable.fromIterable(changes))
        whenever(mainThreadChecker.call()).thenReturn(false)

        try {
            renderer.renderCurrentBuffer()
            fail()
        } catch (expected: IllegalStateException) {
            assertThat(expected).hasMessage("Must be called on correct thread (Main Thread if used with default mainThreadChecker).")
        }

        changes.forEach { change -> verify(change, never()).perform() }
    }

    @Test
    fun renderCurrentBufferRenders() {
        changes.forEach { change -> renderer.render(Observable.just(change)) }
        whenever(mainThreadChecker.call()).thenReturn(true)

        renderer.renderCurrentBuffer()

        changes.forEach { change -> verify(change).perform() }
    }

    @Test
    fun shutdownStopsReactToChoreographerSignals() {
        renderer.render(Observable.fromIterable(changes))
        renderer.shutdown()

        choreographer.simulateCallbacksCall()
        changes.forEach { verifyZeroInteractions(it) }
    }

    class TestChange(private val id: Any) : Change {

        private val performedState = AtomicBoolean()

        val performed: Boolean
            get() = performedState.get()

        override fun perform() {
            if (!performedState.compareAndSet(false, true)) {
                throw IllegalStateException("Change.perform() called second time!")
            }
        }

        override fun equals(other: Any?) = other is TestChange && other.id == this.id

        override fun hashCode() = id.hashCode()
    }
}
