package com.lyft.domic.android.rendering

import android.os.Looper
import android.support.annotation.MainThread
import android.view.Choreographer
import com.lyft.domic.api.rendering.Change
import com.lyft.domic.api.rendering.Renderer
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * It is expected for the application to maintain a singleton of [AndroidRenderer]
 * to optimize resource consumption.
 */
class AndroidRenderer(
        private val choreographer: Choreographer = Choreographer.getInstance(),
        private val timeScheduler: Scheduler = Schedulers.computation(),
        bufferTimeWindowMs: Long = 8, // We'll adjust if needed.
        private val buffer: RenderingBuffer<Change> = RenderingBufferImpl(),
        private val mainThreadChecker: Callable<Boolean> = Callable { Looper.myLooper() == Looper.getMainLooper() }
) : Renderer {

    private val streamDisposables: MutableCollection<StreamDisposable> = ConcurrentLinkedQueue()
    private val disposable: Disposable

    init {
        disposable = Observable
                .interval(0, bufferTimeWindowMs, MILLISECONDS, timeScheduler)
                .filter { buffer.isEmpty() == false }
                .map { buffer.getAndSwap() }
                .subscribe { bufferToRender ->
                    choreographer.postFrameCallback {
                        renderBuffer(bufferToRender)
                    }
                }
    }

    private fun renderBuffer(bufferToRender: Collection<Change>) {
        bufferToRender.forEach { change -> change.perform() }

        // Make sure we don't synchronize on Main Thread.
        timeScheduler.scheduleDirect {
            streamDisposables.forEach { streamDisposable ->
                bufferToRender.forEach { change -> streamDisposable.releaseChange(change) }
            }

            buffer.recycle(bufferToRender)
        }
    }

    override fun render(changes: Observable<out Change>): Disposable {
        val streamDisposable = StreamDisposable()

        val disposable = changes.subscribe { change ->
            buffer.addOrReplace(change)
            streamDisposable.trackChange(change)
        }

        streamDisposable.source = disposable
        streamDisposables.add(streamDisposable)
        return streamDisposable
    }

    /**
     * "Renders" what is in the buffer at the moment, blocking the caller thread.
     *
     * Must be called on Main Thread.
     */
    @MainThread
    override fun renderCurrentBuffer() {
        if (mainThreadChecker.call() == false) {
            throw IllegalStateException("Must be called on correct thread (Main Thread if used with default mainThreadChecker).")
        }

        renderBuffer(buffer.getAndSwap())
    }

    override fun shutdown() = disposable.dispose()

    private inner class StreamDisposable : Disposable {

        // Most of the time it'll hold 1 change at a time since we render each property's stream of values separately.
        // TODO pool of sets.
        private val changes: MutableSet<Change> = Collections.newSetFromMap(ConcurrentHashMap<Change, Boolean>(1))

        lateinit var source: Disposable

        fun trackChange(change: Change) {
            changes.add(change)
        }

        override fun isDisposed() = source.isDisposed

        override fun dispose() {
            buffer.remove(changes)
            source.dispose()
        }

        fun releaseChange(change: Change) {
            changes.remove(change)
        }
    }
}
