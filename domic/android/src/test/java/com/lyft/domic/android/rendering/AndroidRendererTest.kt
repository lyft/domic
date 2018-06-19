package com.lyft.domic.android.rendering

import android.view.Choreographer
import com.lyft.domic.api.rendering.Change
import com.lyft.domic.api.rendering.Renderer
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicBoolean

class AndroidRendererTest {

    private val choreographer = mock<Choreographer>()
    private val timeScheduler = TestScheduler()
    private val bufferTimeWindow = 8L
    private val mainThreadChecker = mock<Callable<Boolean>>()
    private val renderer: Renderer = AndroidRenderer(
            choreographer,
            timeScheduler,
            bufferTimeWindow,
            RenderingBufferImpl(),
            mainThreadChecker
    )
    private val changes = listOf<Change>(mock(), mock(), mock(), mock())

    @Test
    fun postsChangesToChoreographer() {
        whenever(choreographer.postFrameCallback(any())).then { invocation ->
            (invocation.arguments[0] as Choreographer.FrameCallback)
                    .doFrame(0)
        }

        renderer.render(Observable.fromIterable(changes))

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        changes.forEach { change -> verify(change).perform() }
    }

    @Test
    fun buffersChangesInSingleChoreographerCallWithinTimeWindow() {
        changes.forEach { change -> renderer.render(Observable.just(change)) }

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        // Only one aggregated call to Choreographer is expected.
        verify(choreographer, times(1)).postFrameCallback(any())
    }

    @Test
    fun executesChangesBufferedInSingleChoreographerCallWithinTimeWindow() {
        whenever(choreographer.postFrameCallback(any())).then { invocation ->
            (invocation.arguments[0] as Choreographer.FrameCallback)
                    .doFrame(0)
        }

        changes.forEach { change -> renderer.render(Observable.just(change)) }
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        changes.forEach { change -> verify(change, times(1)).perform() }
    }

    @Test
    fun changeCrossedBufferTimeWindowCausesSeparateChoreographerCall() {
        changes.forEach { change ->
            renderer.render(Observable.just(change))
            timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)
        }

        verify(choreographer, times(changes.size)).postFrameCallback(any())
    }

    @Test
    fun doesNotExecuteOldChangesAgain() {
        whenever(choreographer.postFrameCallback(any())).then { invocation ->
            (invocation.arguments[0] as Choreographer.FrameCallback)
                    .doFrame(0)
        }

        val change1 = mock<Change>()
        renderer.render(Observable.just(change1))
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        verify(change1).perform()

        val change2 = mock<Change>()
        renderer.render(Observable.just(change2))
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        verifyNoMoreInteractions(change1)
        verify(change1).perform()
    }

    @Test
    fun changesAreNotExecutedWithoutChoreographerCallback() {
        changes.forEach { change -> renderer.render(Observable.just(change)) }

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        changes.forEach { change -> verify(change, never()).perform() }
    }

    @Test
    fun equalChangesInSameStreamAreDebouncedWithinBufferTimeWindow() {
        whenever(choreographer.postFrameCallback(any())).then { invocation ->
            (invocation.arguments[0] as Choreographer.FrameCallback)
                    .doFrame(0)
        }

        val change1 = TestChange("a")
        val change2 = TestChange("a")

        renderer.render(Observable.fromIterable(listOf(change1, change2)))

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        assertThat(change1.performed).isFalse()
        assertThat(change2.performed).isTrue()
    }

    @Test
    fun equalChangesInDifferentStreamsAreDebouncedWithinBufferTimeWindow() {
        whenever(choreographer.postFrameCallback(any())).then { invocation ->
            (invocation.arguments[0] as Choreographer.FrameCallback)
                    .doFrame(0)
        }

        val change1 = TestChange("a")
        val change2 = TestChange("a")

        renderer.render(Observable.just(change1))
        renderer.render(Observable.just(change2))

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        assertThat(change1.performed).isFalse()
        assertThat(change2.performed).isTrue()
    }

    @Test
    fun changeStreamDisposeRemovesChangesFromBuffer() {
        val disposable = renderer.render(Observable.fromIterable(changes))

        disposable.dispose()

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        verify(choreographer, never()).postFrameCallback(any())
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
    fun shutdownStopsPostingToChoreographer() {
        renderer.render(Observable.fromIterable(changes))
        renderer.shutdown()
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)
        verify(choreographer, never()).postFrameCallback(any())

        renderer.render(Observable.just(mock()))
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)
        verify(choreographer, never()).postFrameCallback(any())
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
