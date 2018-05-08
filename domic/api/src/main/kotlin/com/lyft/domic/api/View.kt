package com.lyft.domic.api

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface View {

    enum class Visibility {
        GONE,
        INVISIBLE,
        VISIBLE,
    }

    val observe: Observe
    val change: Change

    interface Observe {
        val clicks: Observable<Any>
        val focus: Observable<Boolean>
        val longClicks: Observable<Any>
    }

    interface Change {

        fun activated(activatedValues: Observable<Boolean>): Disposable

        fun alpha(alphaValues: Observable<Float>): Disposable

        fun enabled(enabledValues: Observable<Boolean>): Disposable

        fun focusable(focusableValues: Observable<Boolean>): Disposable

        fun focusableInTouchMode(focusableInTouchModeValues: Observable<Boolean>): Disposable

        fun visibility(visibilityValues: Observable<Visibility>): Disposable
    }
}
