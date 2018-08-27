package com.lyft.domic.android

import com.jakewharton.rxbinding2.view.RxView
import com.lyft.domic.android.rendering.mapToChange
import com.lyft.domic.api.View
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.util.sharedDistinctUntilChanged
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidView(
        private val realView: android.view.View,
        private val renderer: Renderer
) : View {

    companion object {
        private const val STATE_INDEX_ACTIVATED = 0
        private const val STATE_INDEX_ALPHA = 1
        private const val STATE_INDEX_ENABLED = 2
        private const val STATE_INDEX_FOCUSABLE = 3
        private const val STATE_INDEX_FOCUSABLE_IN_TOUCH_MODE = 4
        private const val STATE_INDEX_VISIBLE = 5
    }

    private val state = AtomicReferenceArray<Any>(6)

    override val observe = object : View.Observe {

        override val clicks: Observable<Any> by lazy {
            RxView
                    .clicks(realView)
                    .subscribeOn(mainThread())
                    .share()
        }

        override val focus: Observable<Boolean> by lazy {
            RxView
                    .focusChanges(realView)
                    .subscribeOn(mainThread())
                    .share()
        }

        override val longClicks: Observable<Any> by lazy {
            RxView
                    .longClicks(realView)
                    .subscribeOn(mainThread())
                    .share()
        }
    }

    override val change = object : View.Change {

        override fun activated(activatedValues: Observable<Boolean>): Disposable {
            return activatedValues
                    .sharedDistinctUntilChanged(state, STATE_INDEX_ACTIVATED)
                    .mapToChange(realView, STATE_INDEX_ACTIVATED) { realView.isActivated = it }
                    .subscribe(renderer::render)
        }

        override fun alpha(alphaValues: Observable<Float>): Disposable {
            return alphaValues
                    .sharedDistinctUntilChanged(state, STATE_INDEX_ALPHA)
                    .mapToChange(realView, STATE_INDEX_ALPHA) { realView.alpha = it }
                    .subscribe(renderer::render)
        }

        override fun enabled(enabledValues: Observable<Boolean>): Disposable {
            return enabledValues
                    .sharedDistinctUntilChanged(state, STATE_INDEX_ENABLED)
                    .mapToChange(realView, STATE_INDEX_ENABLED) { realView.isEnabled = it }
                    .subscribe(renderer::render)
        }

        override fun focusable(focusableValues: Observable<Boolean>): Disposable {
            return focusableValues
                    .sharedDistinctUntilChanged(state, STATE_INDEX_FOCUSABLE)
                    .mapToChange(realView, STATE_INDEX_FOCUSABLE) { realView.isFocusable = it }
                    .subscribe(renderer::render)
        }

        override fun focusableInTouchMode(focusableInTouchModeValues: Observable<Boolean>): Disposable {
            return focusableInTouchModeValues
                    .sharedDistinctUntilChanged(state, STATE_INDEX_FOCUSABLE_IN_TOUCH_MODE)
                    .mapToChange(realView, STATE_INDEX_FOCUSABLE_IN_TOUCH_MODE) { realView.isFocusableInTouchMode = it }
                    .subscribe(renderer::render)
        }

        override fun visibility(visibilityValues: Observable<View.Visibility>): Disposable {
            return visibilityValues
                    .sharedDistinctUntilChanged(state, STATE_INDEX_VISIBLE)
                    .mapToChange(realView, STATE_INDEX_VISIBLE) {
                        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                        realView.visibility = when (it) {
                            View.Visibility.GONE -> android.view.View.GONE
                            View.Visibility.INVISIBLE -> android.view.View.INVISIBLE
                            View.Visibility.VISIBLE -> android.view.View.VISIBLE
                        }
                    }
                    .subscribe(renderer::render)
        }
    }
}
