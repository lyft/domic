package com.lyft.domic.api

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action

interface Renderer {

    /**
     * "Renders" stream of changes.
     *
     * Implementation can be opinionated on grouping and/or ordering of execution.
     *
     * Each action's `Action.run()` will be called once on proper thread (ie Main Thread).
     */
    fun render(actions: Observable<Action>): Disposable

    fun shutdown()
}

inline fun Observable<Action>.subscribe(crossinline func: (stream: Observable<Action>) -> Disposable)
        = func.invoke(this)
