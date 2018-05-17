package com.lyft.domic.android

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

internal class RenderingBufferImpl<T> : RenderingBuffer<T> {

    private val buffer1: MutableCollection<T> = ArrayList(20)
    private val buffer2: MutableCollection<T> = ArrayList(20)
    private var currentBuffer: MutableCollection<T> = buffer1
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

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

    override fun swapAndGetSnapshot(): Collection<T> {
        return lock.writeLock().run {
            lock()
            val snapshot = currentBuffer

            when (snapshot) {
                buffer1 -> currentBuffer = buffer2
                buffer2 -> currentBuffer = buffer1
            }

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
}
