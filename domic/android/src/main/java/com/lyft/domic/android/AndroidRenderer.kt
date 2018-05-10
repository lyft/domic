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
import kotlin.collections.ArrayList

class AndroidRenderer(
        private val choreographer: Choreographer,
        timeScheduler: Scheduler,
        bufferTimeWindowMs: Long
) : Renderer {

    companion object {
        private val INSTANCE by lazy {
            AndroidRenderer(Choreographer.getInstance(), Schedulers.computation(), 8)
        }

        fun getInstance(): AndroidRenderer = INSTANCE
    }

    // TODO make a pool of objects similar to Message.obtain() to reduce allocations.
    private data class Actionw(
            val streamId: Int,
            val action: Action
    )

    // Guarded by itself.
    // TODO implement buffer swapping to avoid allocation and reduce synchronization time.
    private val buffer: MutableList<Actionw> = ArrayList(20)

    private val streamIdGenerator = AtomicInteger()

    private val disposable: Disposable

    init {
        disposable = Observable
                .interval(0, bufferTimeWindowMs, MILLISECONDS, timeScheduler) // We'll adjust if needed.
                .filter { buffer.size != 0 }
                .map {
                    // Reading `buffer.size` is ok, might not match end size due to concurrent modification.
                    val copy: MutableList<Actionw> = ArrayList(buffer.size)

                    synchronized(buffer) {
                        copy.addAll(buffer)
                        buffer.clear()
                    }

                    copy
                }
                .subscribe { actions ->
                    choreographer.postFrameCallback {
                        actions.forEach { actionw -> actionw.action.run() }
                    }
                }
    }

    override fun render(actions: Observable<Action>): Disposable {
        val streamId = streamIdGenerator.incrementAndGet()

        val disposable = actions.subscribe { action ->
            val actionw = Actionw(streamId, action)

            synchronized(buffer) {
                buffer.add(actionw)
            }
        }

        return ActionStreamDisposable(streamId, disposable)
    }

    override fun shutdown() = disposable.dispose()

    private inner class ActionStreamDisposable(val streamId: Int, val source: Disposable) : Disposable {
        override fun isDisposed() = source.isDisposed

        override fun dispose() {
            source.dispose()

            synchronized(buffer) {
                buffer.removeAll { it.streamId == this.streamId }
            }
        }
    }
}
