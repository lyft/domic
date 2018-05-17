package com.lyft.domic.android.rendering

class RenderingBufferImplTest : AbstractRenderingBufferTest() {
    override fun <T> createRenderingBuffer(): RenderingBuffer<T> = RenderingBufferImpl()
}