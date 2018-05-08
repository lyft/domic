package com.lyft.domic.api

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface EditText :TextView {

    override val observe: Observe
    override val change: Change

    interface Observe : TextView.Observe

    interface Change : TextView.Change {

        fun selection(selectionValues: Observable<Int>): Disposable
    }
}
