package com.lyft.domic.android

/**
 * Rendering buffer, all methods must be thread-safe.
 *
 * See https://en.wikipedia.org/wiki/Multiple_buffering#Double_buffering_in_computer_graphics
 */
interface RenderingBuffer<T> {

    /**
     * Adds or replaces an item in *current* underlying buffer. Relies on item's [equals].
     */
    fun addOrReplace(item: T)

    /**
     * Checks if *current* underlying buffer empty.
     */
    fun isEmpty(): Boolean

    /**
     * Atomically swaps *current* underlying buffer with another.
     * Subsequent calls will work against another buffer until it's swapped back.
     *
     * - Returned buffer must not be modified.
     * - Returned buffer is safe to read if you can guarantee that [swapAndGetSnapshot]
     * won't be called during read.
     *
     * See [Double Buffering in Computer Graphics](https://en.wikipedia.org/wiki/Multiple_buffering#Double_buffering_in_computer_graphics).
     *
     * @return read-only view to *current* underlying buffer.
     */
    fun swapAndGetSnapshot(): Collection<T>

    /**
     * Removes item from *current* underlying buffer. Relies on item's [equals].
     */
    fun remove(item: T)
}
