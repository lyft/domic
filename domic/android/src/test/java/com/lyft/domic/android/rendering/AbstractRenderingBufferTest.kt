package com.lyft.domic.android.rendering

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

abstract class AbstractRenderingBufferTest {

    abstract fun <T> createRenderingBuffer(): RenderingBuffer<T>

    @Test
    fun addOrReplaceAddsItem() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")

        assertThat(buffer.swapAndGet()).isEqualTo(listOf("a"))
    }

    @Test
    fun addOrReplaceReplacesItem() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")
        buffer.addOrReplace("a")

        assertThat(buffer.swapAndGet()).isEqualTo(listOf("b", "a"))
    }

    @Test
    fun isEmptyTrue() {
        val buffer = createRenderingBuffer<Any>()

        assertThat(buffer.isEmpty()).isTrue()
    }

    @Test
    fun isEmptyFalse() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")

        assertThat(buffer.isEmpty()).isFalse()
    }

    @Test
    fun swapAndGetSnapshotReturnsCurrentBuffer() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        val snapshot = buffer.swapAndGet()

        assertThat(snapshot).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun swapAndGetSnapshotSwapsBuffer() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        buffer.swapAndGet()

        buffer.addOrReplace("c")
        buffer.addOrReplace("d")

        val snapshot = buffer.swapAndGet()

        assertThat(snapshot).isEqualTo(listOf("c", "d"))
    }

    @Test
    fun swapAndGetSnapshotClearsPreviousBuffer() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        buffer.swapAndGet()

        buffer.addOrReplace("c")
        buffer.addOrReplace("d")

        buffer.swapAndGet()
        val snapshot2 = buffer.swapAndGet()

        assertThat(snapshot2).isEmpty()
    }

    @Test
    fun removeRemovesMatchingItem() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")
        buffer.addOrReplace("c")

        buffer.remove("b")

        assertThat(buffer.swapAndGet()).isEqualTo(listOf("a", "c"))
    }

    @Test
    fun recycleReusesBufferForSwapAndGet() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        val b1 = buffer.swapAndGet()

        buffer.recycle(b1)

        buffer.swapAndGet()
        val b3 = buffer.swapAndGet()

        assertThat(b1).isSameAs(b3)
    }

    @Test
    fun recycleClearsBuffer() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        val b1 = buffer.swapAndGet()

        buffer.recycle(b1)

        assertThat(b1).isEmpty()
    }

}