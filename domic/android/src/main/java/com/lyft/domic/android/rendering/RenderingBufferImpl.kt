package com.lyft.domic.android.rendering

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

internal class RenderingBufferImpl<T> : RenderingBuffer<T> {

    private val lock: ReadWriteLock = ReentrantReadWriteLock()
    private val bufferPool = ArrayList<MutableCollection<T>>(3)
    private var currentBuffer: MutableCollection<T> = obtainBuffer()

    override fun addOrReplace(item: T) {
        lock.writeLock().withLock {
            currentBuffer.remove(item)
            currentBuffer.add(item)
        }
    }

    override fun isEmpty(): Boolean {
        return lock.readLock().withLock {
            val result = currentBuffer.isEmpty()
            result
        }
    }

    override fun getAndSwap(): Collection<T> {
        return lock.writeLock().withLock {
            val snapshot = currentBuffer
            currentBuffer = obtainBuffer()

            snapshot
        }
    }

    override fun remove(item: T) {
        lock.writeLock().withLock {
            currentBuffer.remove(item)
        }
    }

    override fun remove(items: Collection<T>) {
        lock.writeLock().withLock {
            currentBuffer.removeAll(items)
        }
    }

    override fun recycle(buffer: Collection<T>) {
        lock.writeLock().withLock {
            buffer as MutableCollection<T>
            buffer.clear()
            bufferPool.add(buffer)
        }
    }

    private fun obtainBuffer(): MutableCollection<T> {
        return lock.writeLock().withLock {
            if (bufferPool.isEmpty()) {
                ArrayList(20)
            } else {
                // Poll last item to avoid array copy inside ArrayList.
                val index = bufferPool.size - 1

                val bp = bufferPool[index]
                bufferPool.removeAt(index)

                bp
            }
        }
    }
}
