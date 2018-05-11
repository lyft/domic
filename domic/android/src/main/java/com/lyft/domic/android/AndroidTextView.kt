package com.lyft.domic.android

import com.jakewharton.rxbinding2.widget.RxTextView
import com.lyft.domic.api.Renderer
import com.lyft.domic.api.TextView
import com.lyft.domic.api.View
import com.lyft.domic.api.subscribe
import com.lyft.domic.util.distinctUntilChanged
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidTextView(
        private val realTextView: android.widget.TextView,
        private val renderer: Renderer
) : TextView {

    private val asView: View = AndroidView(realTextView, renderer)

    override val observe: TextView.Observe = object : TextView.Observe, View.Observe by asView.observe {

        override val textChanges: Observable<out CharSequence> by lazy {
            RxTextView
                    .textChanges(realTextView)
                    .subscribeOn(mainThread())
                    .share()
        }

        override val textChangeEvents: Observable<Any> by lazy {
            RxTextView
                    .textChangeEvents(realTextView)
                    .subscribeOn(mainThread())
                    .map { _ -> Any() } // TODO own event data class.
                    .share()
        }
    }

    override val change: TextView.Change = object : TextView.Change, View.Change by asView.change {

        private val state = AtomicReferenceArray<Any>(1)

        override fun text(textValues: Observable<out CharSequence>): Disposable = textValues
                .distinctUntilChanged(state, 0)
                .map { Action { realTextView.text = it } }
                .subscribe(renderer::render)
    }
}
