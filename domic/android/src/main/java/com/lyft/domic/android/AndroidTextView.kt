package com.lyft.domic.android

import android.os.Build
import android.text.PrecomputedText
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxbinding2.widget.RxTextView
import com.lyft.domic.android.annotations.MutatedByFramework
import com.lyft.domic.android.rendering.mapToChange
import com.lyft.domic.api.TextView
import com.lyft.domic.api.View
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.util.sharedDistinctUntilChanged
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
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

        private val textMetricsParams: Observable<android.text.PrecomputedText.Params> by lazy {
            Observable
                    .fromCallable {
                        if (Build.VERSION.SDK_INT >= 28) {
                            realTextView.textMetricsParams
                        } else {
                            throw IllegalStateException("PrecomputedText is not supported on API below 28.")
                        }
                    }
                    .subscribeOn(mainThread())
                    .replayingShare()
        }

        override fun text(textValues: Observable<out CharSequence>): Disposable {
            var values: Observable<out CharSequence> = textValues
                    .sharedDistinctUntilChanged(state, STATE_INDEX_TEXT)

            if (Build.VERSION.SDK_INT >= 28) {
                values = Observable
                        .combineLatest(values, textMetricsParams, BiFunction { text, metrics -> PrecomputedText.create(text, metrics) })
            }

            return values
                    .mapToChange(realTextView, STATE_INDEX_TEXT) { realTextView.text = it }
                    .subscribe(renderer::render)
        }
    }

    init {
        observe
                .textChanges
                // TODO: SpannableStringBuilder that comes from framework callbacks returns false if compared with same-value String, should we map value to String?
                .subscribe { state.set(STATE_INDEX_TEXT, it) }
    }
}
