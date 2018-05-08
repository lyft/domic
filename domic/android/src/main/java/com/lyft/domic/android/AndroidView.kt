package com.lyft.domic.android

import com.jakewharton.rxbinding2.view.RxView
import com.lyft.domic.api.View
import com.lyft.domic.util.distinctUntilChanged
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidView(private val realView: android.view.View) : View {

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
                    .observeOn(mainThread())
                    .subscribe { realView.isActivated = it }
        }

        override fun alpha(alphaValues: Observable<Float>): Disposable {
            return alphaValues
                    .distinctUntilChanged(state, 1)
                    .observeOn(mainThread())
                    .subscribe { realView.alpha = it }
        }

        override fun enabled(enabledValues: Observable<Boolean>): Disposable {
            return enabledValues
                    .distinctUntilChanged(state, 2)
                    .observeOn(mainThread())
                    .subscribe { realView.isEnabled = it }
        }

        override fun focusable(focusableValues: Observable<Boolean>): Disposable {
            return focusableValues
                    .distinctUntilChanged(state, 3)
                    .observeOn(mainThread())
                    .subscribe { realView.isFocusable = it }
        }

        override fun focusableInTouchMode(focusableInTouchModeValues: Observable<Boolean>): Disposable {
            return focusableInTouchModeValues
                    .distinctUntilChanged(state, 4)
                    .observeOn(mainThread())
                    .subscribe { realView.isFocusableInTouchMode = it }
        }

        override fun visibility(visibilityValues: Observable<View.Visibility>): Disposable {
            return visibilityValues
                    .distinctUntilChanged(state, 5)
                    .observeOn(mainThread())
                    .subscribe {
                        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                        realView.visibility = when (it) {
                            View.Visibility.GONE -> android.view.View.GONE
                            View.Visibility.INVISIBLE -> android.view.View.INVISIBLE
                            View.Visibility.VISIBLE -> android.view.View.VISIBLE
                        }
                    }
        }
    }
}
