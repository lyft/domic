package com.lyft.domic.api

interface Button : TextView {

    override val observe: Observe
    override val change: Change

    interface Observe : TextView.Observe

    interface Change : TextView.Change
}
