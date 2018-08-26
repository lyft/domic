package com.lyft.domic.android.rendering

import android.view.Choreographer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever

class TestChoreographer {

    // Choreographer is a final class, we can't implement it as interface directly.
    val choreographer: Choreographer = mock()

    private val currentFrameCallbacks = mutableSetOf<Choreographer.FrameCallback>()
    private var frameTimeNanos = 0L

    init {
        whenever(choreographer.postFrameCallback(any())).then { invocation ->
            currentFrameCallbacks += invocation.arguments.first() as Choreographer.FrameCallback
            Unit
        }
    }

    fun callbacksCount(): Int = currentFrameCallbacks.size

    /**
     * Simulates call from Android Framework, callbacks are disposed to mimic Framework.
     */
    fun simulateCallbacksCall() {
        val callbacksCopy = HashSet<Choreographer.FrameCallback>(currentFrameCallbacks)
        currentFrameCallbacks.clear()
        callbacksCopy.forEach { it.doFrame(frameTimeNanos++) }
    }
}
