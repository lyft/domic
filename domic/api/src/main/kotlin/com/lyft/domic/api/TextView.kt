package com.lyft.domic.api

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface TextView : View {

    override val observe: Observe
    override val change: Change

    interface Observe : View.Observe {
        val textChanges: Observable<out CharSequence>

        // TODO: delete, suggest to map [textChanges] to [Unit].
        val textChangeEvents: Observable<Any>
    }

    interface Change : View.Change {

        fun text(textValues: Observable<out CharSequence>): Disposable
    }
}

