package com.lyft.domic.api

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface CompoundButton : Button {

    override val observe: Observe
    override val change: Change

    interface Observe : Button.Observe {
        val checked: Observable<out Boolean>
    }

    interface Change : Button.Change {
        fun checked(checkedValues: Observable<Boolean>): Disposable
    }
}