package com.lyft.domic.sampleapp.signin

sealed class AnimationEvent<T> {

    object Started : AnimationEvent<Any>()

    data class Intermediate<T>(val value: T) : AnimationEvent<T>()

    object Cancelled : AnimationEvent<Any>()

    object Ended : AnimationEvent<Any>()
}


data class Animation<T>(val value: T, val durationMillis: Long, val intermediateValueIntervalMillis: Long)
