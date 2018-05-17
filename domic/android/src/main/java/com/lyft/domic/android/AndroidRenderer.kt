package com.lyft.domic.android

import android.view.Choreographer
import com.lyft.domic.api.Renderer
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.*
import java.util.concurrent.atomic.AtomicInteger

class AndroidRenderer(
        private val choreographer: Choreographer = Choreographer.getInstance(),
        timeScheduler: Scheduler = Schedulers.computation(),
        bufferTimeWindowMs: Long = 8, // We'll adjust if needed.
        private val buffer: RenderingBuffer<Action> = RenderingBufferImpl()
) : Renderer {

    companion object {
        private val EMPTY_ACTION = Action { }
        private val INSTANCE by lazy { AndroidRenderer() }

        fun getInstance(): AndroidRenderer = INSTANCE
    }

    // TODO make a pool of objects similar to Message.obtain() to reduce allocations?
    internal class Actionw(private val streamId: Int, private val source: Action) : Action {

        override fun run() = source.run()

        override fun equals(other: Any?): Boolean =
                other === this
                        ||
                        (other?.hashCode() == streamId && other is Actionw)

        override fun hashCode() = streamId
    }

    private val streamIdGenerator = AtomicInteger()

    private val disposable: Disposable

    init {
        disposable = Observable
                .interval(0, bufferTimeWindowMs, MILLISECONDS, timeScheduler)
                .filter { buffer.isEmpty() == false }
                .map { buffer.swapAndGetSnapshot() }
                .subscribe { actions ->
                    choreographer.postFrameCallback { actions.forEach { it.run() } }
                }
    }

    override fun render(actions: Observable<Action>): Disposable {
        val streamId = streamIdGenerator.incrementAndGet()

        val disposable = actions.subscribe { action ->
            buffer.addOrReplace(Actionw(streamId, action))
        }

        return ActionStreamDisposable(
                Actionw(
                        streamId,

                        /** We don't need to maintain reference to actual action because equals will check streamId. */
                        EMPTY_ACTION
                ),
                disposable
        )
    }

    override fun shutdown() = disposable.dispose()

    private inner class ActionStreamDisposable(val actionw: Actionw, val source: Disposable) : Disposable {
        override fun isDisposed() = source.isDisposed

        override fun dispose() {
            buffer.remove(actionw)
            source.dispose()
        }
    }
}
