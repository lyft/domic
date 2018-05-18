package com.lyft.domic.android.rendering

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

abstract class AbstractRenderingBufferTest {

    abstract fun <T> createRenderingBuffer(): RenderingBuffer<T>

    @Test
    fun addOrReplaceAddsItem() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")

        assertThat(buffer.getAndSwap()).isEqualTo(listOf("a"))
    }

    @Test
    fun addOrReplaceReplacesItem() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")
        buffer.addOrReplace("a")

        assertThat(buffer.getAndSwap()).isEqualTo(listOf("b", "a"))
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
    fun getAndSwapReturnsCurrentBuffer() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        val snapshot = buffer.getAndSwap()

        assertThat(snapshot).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun getAndSwapSwapsBuffer() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        buffer.getAndSwap()

        buffer.addOrReplace("c")
        buffer.addOrReplace("d")

        val snapshot = buffer.getAndSwap()

        assertThat(snapshot).isEqualTo(listOf("c", "d"))
    }

    @Test
    fun getAndSwapClearsPreviousBuffer() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        buffer.getAndSwap()

        buffer.addOrReplace("c")
        buffer.addOrReplace("d")

        buffer.getAndSwap()
        val snapshot2 = buffer.getAndSwap()

        assertThat(snapshot2).isEmpty()
    }

    @Test
    fun removeRemovesMatchingItem() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")
        buffer.addOrReplace("c")

        buffer.remove("b")

        assertThat(buffer.getAndSwap()).isEqualTo(listOf("a", "c"))
    }

    @Test
    fun recycleReusesBufferForgetAndSwap() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        val b1 = buffer.getAndSwap()

        buffer.recycle(b1)

        buffer.getAndSwap()
        val b3 = buffer.getAndSwap()

        assertThat(b1).isSameAs(b3)
    }

    @Test
    fun recycleClearsBuffer() {
        val buffer = createRenderingBuffer<String>()

        buffer.addOrReplace("a")
        buffer.addOrReplace("b")

        val b1 = buffer.getAndSwap()

        buffer.recycle(b1)

        assertThat(b1).isEmpty()
    }

}
