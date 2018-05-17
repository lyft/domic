package com.lyft.domic.android.rendering

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

internal class RenderingBufferImpl<T> : RenderingBuffer<T> {

    private val lock: ReadWriteLock = ReentrantReadWriteLock()
    private val bufferPool: MutableList<MutableCollection<T>> = ArrayList(3)
    private var currentBuffer: MutableCollection<T> = obtainBuffer()

    override fun addOrReplace(item: T) {
        lock.writeLock().apply {
            lock()
            currentBuffer.remove(item)
            currentBuffer.add(item)
            unlock()
        }
    }

    override fun isEmpty(): Boolean {
        return lock.readLock().run {
            lock()
            val result = currentBuffer.isEmpty()
            unlock()
            result
        }
    }

    override fun swapAndGet(): Collection<T> {
        return lock.writeLock().run {
            lock()
            val snapshot = currentBuffer
            currentBuffer = obtainBuffer()

            unlock()
            snapshot
        }
    }

    override fun remove(item: T) {
        lock.writeLock().apply {
            lock()
            currentBuffer.remove(item)
            unlock()
        }
    }

    override fun recycle(buffer: Collection<T>) {
        lock.writeLock().apply {
            lock()
            buffer as MutableCollection<T>
            buffer.clear()
            bufferPool.add(buffer)
            unlock()
        }
    }

    private fun obtainBuffer(): MutableCollection<T> {
        return lock.writeLock().run {
            lock()

            val result = if (bufferPool.isEmpty()) {
                ArrayList(20)
            } else {

                val bp = bufferPool[0]
                bufferPool.removeAt(0)
                bp
            }

            unlock()

            result
        }
    }
}
