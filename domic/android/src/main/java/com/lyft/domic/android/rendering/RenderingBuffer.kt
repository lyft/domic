package com.lyft.domic.android.rendering

/**
 * Rendering buffer, all methods must be thread-safe.
 *
 * See https://en.wikipedia.org/wiki/Multiple_buffering#Double_buffering_in_computer_graphics
 */
interface RenderingBuffer<T> {

    /**
     * Adds or replaces an item in *current* underlying buffer. Relies on item's [equals].
     *
     * In case of replacement item should be added to the tail of the buffer.
     */
    fun addOrReplace(item: T)

    /**
     * Checks if *current* underlying buffer empty.
     */
    fun isEmpty(): Boolean

    /**
     * Atomically swaps *current* underlying buffer with another and returns the one that was
     * *current* before the swap.
     *
     * Subsequent reads and writes on this [RenderingBuffer] will work against another buffer
     * until it's swapped.
     * "Another" buffer is guaranteed to be empty.
     *
     * - Returned buffer must not be modified.
     * - Returned buffer should be recycled after use via [recycle].
     * - Returned buffer is safe to read if it's not yet recycled via [recycle].
     *
     * See [Double Buffering in Computer Graphics](https://en.wikipedia.org/wiki/Multiple_buffering#Double_buffering_in_computer_graphics).
     *
     * @return read-only view to *current* underlying buffer.
     */
    fun getAndSwap(): Collection<T>

    /**
     * Removes item from *current* underlying buffer. Relies on item's [equals].
     */
    fun remove(item: T)

    /**
     * Removes items from *current* underlying buffer. Relies on item's [equals].
     */
    fun remove(items: Collection<T>)

    /**
     * Recycles used buffer into internal pool, it's illegal to use buffer after recycling.
     */
    fun recycle(buffer: Collection<T>)
}
