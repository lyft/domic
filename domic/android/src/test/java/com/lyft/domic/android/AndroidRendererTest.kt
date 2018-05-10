package com.lyft.domic.android

import android.view.Choreographer
import com.lyft.domic.api.Renderer
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.functions.Action
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit.*

class AndroidRendererTest {

    private val choreographer = mock<Choreographer>()
    private val timeScheduler = TestScheduler()
    private val bufferTimeWindow = 8L
    private val renderer: Renderer = AndroidRenderer(choreographer, timeScheduler, bufferTimeWindow)
    private val actions = listOf<Action>(mock(), mock(), mock(), mock())

    @Test
    fun postsActionsToChoreographer() {
        whenever(choreographer.postFrameCallback(any())).then { invocation ->
            (invocation.arguments[0] as Choreographer.FrameCallback)
                    .doFrame(0)
        }

        renderer.render(Observable.fromIterable(actions))

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        actions.forEach { action -> verify(action, times(1)).run() }
    }

    @Test
    fun buffersActionsInTimeWindow() {
        renderer.render(Observable.fromIterable(actions))

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        // Only one aggregated call to Choreographer is expected.
        verify(choreographer, times(1)).postFrameCallback(any())
    }

    @Test
    fun actionCrossedBufferTimeWindowCausesSeparateChoreographerCall() {
        renderer.render(Observable.just(mock()))
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        renderer.render(Observable.just(mock()))
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        verify(choreographer, times(2)).postFrameCallback(any())
    }

    @Test
    fun actionsAcrossBufferTimeWindowGetsExecutedInOrder() {
        whenever(choreographer.postFrameCallback(any())).then { invocation ->
            (invocation.arguments[0] as Choreographer.FrameCallback)
                    .doFrame(0)
        }

        val action1 = mock<Action>()
        renderer.render(Observable.just(action1))
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        verify(action1).run()

        val action2 = mock<Action>()
        renderer.render(Observable.just(action2))
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        verifyNoMoreInteractions(action1)
        verify(action1).run()
    }

    @Test
    fun actionsAreNotExecutedWithoutChoreographerCallback() {
        renderer.render(Observable.fromIterable(actions))

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        actions.forEach { action -> verify(action, never()).run() }
    }

    @Test
    fun actionStreamDisposeRemovesActionFromBuffer() {
        val disposable = renderer.render(Observable.fromIterable(actions))

        disposable.dispose()

        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)

        verify(choreographer, never()).postFrameCallback(any())
    }

    @Test
    fun shutdownStopsPostingToChoreographer() {
        renderer.render(Observable.fromIterable(actions))
        renderer.shutdown()
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)
        verify(choreographer, never()).postFrameCallback(any())

        renderer.render(Observable.just(mock()))
        timeScheduler.advanceTimeBy(bufferTimeWindow, MILLISECONDS)
        verify(choreographer, never()).postFrameCallback(any())
    }
}
