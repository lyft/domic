package com.lyft.domic.android

import com.jakewharton.rxbinding2.view.RxView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.View
import com.lyft.domic.api.subscribe
import com.lyft.domic.util.distinctUntilChanged
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidView(
        private val realView: android.view.View,
        private val renderer: Renderer
) : View {

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

        private val state = AtomicReferenceArray<Any>(6)

        override fun activated(activatedValues: Observable<Boolean>): Disposable {
            return activatedValues
                    .distinctUntilChanged(state, 0)
                    .map { Action { realView.isActivated = it } }
                    .subscribe(renderer::render)
        }

        override fun alpha(alphaValues: Observable<Float>): Disposable {
            return alphaValues
                    .distinctUntilChanged(state, 1)
                    .map { Action { realView.alpha = it } }
                    .subscribe(renderer::render)
        }

        override fun enabled(enabledValues: Observable<Boolean>): Disposable {
            return enabledValues
                    .distinctUntilChanged(state, 2)
                    .map { Action { realView.isEnabled = it } }
                    .subscribe(renderer::render)
        }

        override fun focusable(focusableValues: Observable<Boolean>): Disposable {
            return focusableValues
                    .distinctUntilChanged(state, 3)
                    .map { Action { realView.isFocusable = it } }
                    .subscribe(renderer::render)
        }

        override fun focusableInTouchMode(focusableInTouchModeValues: Observable<Boolean>): Disposable {
            return focusableInTouchModeValues
                    .distinctUntilChanged(state, 4)
                    .map { Action { realView.isFocusableInTouchMode = it } }
                    .subscribe(renderer::render)
        }

        override fun visibility(visibilityValues: Observable<View.Visibility>): Disposable {
            return visibilityValues
                    .distinctUntilChanged(state, 5)
                    .map {
                        Action {
                            @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                            realView.visibility = when (it) {
                                View.Visibility.GONE -> android.view.View.GONE
                                View.Visibility.INVISIBLE -> android.view.View.INVISIBLE
                                View.Visibility.VISIBLE -> android.view.View.VISIBLE
                            }
                        }
                    }
                    .subscribe(renderer::render)
        }
    }
}
