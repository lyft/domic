package com.lyft.domic.api.rendering

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action

interface Renderer {

    /**
     * "Renders" stream of changes.
     *
     * Implementation can be opinionated on grouping, and/or ordering of execution.
     *
     * Each action's `Action.run()` will be called once on proper thread (ie Main Thread).
     *
     * TODO add @param reduce if `true` allows [Renderer] to reduce actions emitted by single [Observable]
     * to be reduced down to last one within buffering time window thus reducing amount of actions
     * that need to be rendered.
     *
     * @return [Disposable] that allows to stop observing the [Observable]. [Renderer] should try
     * to remove observed but not rendered actions from current buffer.
     */
    fun render(actions: Observable<Action>): Disposable

    /**
     * Shuts down the [Renderer]. [Renderer] can not be reused after shut down.
     */
    fun shutdown()
}
