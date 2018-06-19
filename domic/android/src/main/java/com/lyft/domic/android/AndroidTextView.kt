package com.lyft.domic.android

import com.jakewharton.rxbinding2.widget.RxTextView
import com.lyft.domic.android.annotations.MutatedByFramework
import com.lyft.domic.android.rendering.mapToChange
import com.lyft.domic.api.TextView
import com.lyft.domic.api.View
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.util.distinctUntilChanged
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidTextView(
        private val realTextView: android.widget.TextView,
        private val renderer: Renderer
) : TextView {

    companion object {
        private const val STATE_INDEX_TEXT = 0
    }

    private val asView: View = AndroidView(realTextView, renderer)
    private val state = AtomicReferenceArray<Any>(1)

    override val observe: TextView.Observe = object : TextView.Observe, View.Observe by asView.observe {

        @MutatedByFramework
        // TODO: rename to 'text'?
        override val textChanges: Observable<out CharSequence> =
                RxTextView
                        .textChanges(realTextView)
                        .subscribeOn(mainThread())
                        .share()

        override val textChangeEvents: Observable<Any> by lazy {
            RxTextView
                    .textChangeEvents(realTextView)
                    .subscribeOn(mainThread())
                    .map { _ -> Any() } // TODO own event data class.
                    .share()
        }
    }

    override val change: TextView.Change = object : TextView.Change, View.Change by asView.change {

        override fun text(textValues: Observable<out CharSequence>): Disposable = textValues
                .distinctUntilChanged(state, STATE_INDEX_TEXT)
                .mapToChange(realTextView, STATE_INDEX_TEXT) { realTextView.text = it }
                .subscribe(renderer::render)
    }

    init {
        observe
                .textChanges
                // TODO: SpannableStringBuilder that comes from framework callbacks returns false if compared with same-value String, should we map value to String?
                .subscribe { state.set(STATE_INDEX_TEXT, it) }
    }
}
