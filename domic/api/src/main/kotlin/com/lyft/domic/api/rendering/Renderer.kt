package com.lyft.domic.api.rendering

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface Renderer {

    /**
     * "Renders" stream of changes.
     *
     * Implementation can be opinionated on grouping, and/or ordering of execution.
     *
     * [Renderer] might use [Change.equals] and [Change.hashCode] to compare [Change]s and optimize
     * rendering pipeline based on that, for example render latest equal [Change] arrived
     * within same buffering window.
     *
     * Each [Change]'s [Change.perform] will be called once on proper thread (Main Thread by default).
     *
     * @return [Disposable] that allows to stop observing the [Observable]. [Renderer] should try
     * to remove observed but not yet rendered actions from current buffer.
     */
    fun render(changes: Observable<out Change>): Disposable

    /**
     * "Renders" what is in the buffer at the moment, blocking the caller thread.
     *
     * Main purpose of this API is to add a way of solving ["First Frame Problem"](https://github.com/lyft/domic/issues/14).
     *
     * Implementation can be opinionated on threading and throw an exception if called on wrong thread.
     */
    fun renderCurrentBuffer()

    /**
     * Shuts down the [Renderer]. [Renderer] can not be reused after shut down.
     */
    fun shutdown()
}
