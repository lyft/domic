package com.lyft.domic.test

import com.lyft.domic.api.View
import com.lyft.domic.util.sharedDistinctUntilChanged
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReferenceArray

class TestView : View {

    interface Check {
        val activated: Boolean?

        val alpha: Float?

        val enabled: Boolean?

        val focus: Boolean?

        val focusable: Boolean?

        val focusableInTouchMode: Boolean?

        val visibility: View.Visibility?
    }

    interface Simulate {
        fun click()
        fun focus(focusValue: Boolean)
        fun longClick()
    }

    private val activatedRelay = TestPublishRelay.create<Boolean>()
    private val alphaRelay = TestPublishRelay.create<Float>()
    private val clicksRelay = TestPublishRelay.create<Any>()
    private val enabledRelay = TestPublishRelay.create<Boolean>()
    private val focusRelay = TestPublishRelay.create<Boolean>()
    private val focusableRelay = TestPublishRelay.create<Boolean>()
    private val focusableInTouchModeRelay = TestPublishRelay.create<Boolean>()
    private val longClicksRelay = TestPublishRelay.create<Any>()
    private val visibilityRelay = TestPublishRelay.create<View.Visibility>()

    override val observe: View.Observe = object : View.Observe {
        override val clicks: Observable<Any> = clicksRelay
        override val focus: Observable<Boolean> = focusRelay
        override val longClicks: Observable<Any> = longClicksRelay
    }

    override val change: View.Change = object : View.Change {

        private val state = AtomicReferenceArray<Any>(6)

        override fun activated(activatedValues: Observable<Boolean>): Disposable {
            return activatedValues
                    .sharedDistinctUntilChanged(state, 0)
                    .subscribe(activatedRelay)
        }

        override fun alpha(alphaValues: Observable<Float>): Disposable {
            return alphaValues
                    .sharedDistinctUntilChanged(state, 1)
                    .subscribe(alphaRelay)
        }

        override fun enabled(enabledValues: Observable<Boolean>): Disposable {
            return enabledValues
                    .sharedDistinctUntilChanged(state, 2)
                    .subscribe(enabledRelay)
        }

        override fun focusable(focusableValues: Observable<Boolean>): Disposable {
            return focusableValues
                    .sharedDistinctUntilChanged(state, 3)
                    .subscribe(focusableRelay)
        }

        override fun focusableInTouchMode(focusableInTouchModeValues: Observable<Boolean>): Disposable {
            return focusableInTouchModeValues
                    .sharedDistinctUntilChanged(state, 4)
                    .subscribe(focusableInTouchModeRelay)
        }

        override fun visibility(visibilityValues: Observable<View.Visibility>): Disposable {
            return visibilityValues
                    .sharedDistinctUntilChanged(state, 5)
                    .subscribe(visibilityRelay)
        }
    }

    val check: Check = object : Check {

        override val activated: Boolean?
            get() = activatedRelay.lastValue()

        override val alpha: Float?
            get() = alphaRelay.lastValue()

        override val enabled: Boolean?
            get() = enabledRelay.lastValue()

        override val focus: Boolean?
            get() = focusRelay.lastValue()

        override val focusable: Boolean?
            get() = focusableRelay.lastValue()

        override val focusableInTouchMode: Boolean?
            get() = focusableInTouchModeRelay.lastValue()

        override val visibility: View.Visibility?
            get() = visibilityRelay.lastValue()
    }

    val simulate: Simulate = object : Simulate {

        override fun click() {
            clicksRelay.accept(Any())
        }

        override fun focus(focusValue: Boolean) {
            focusRelay.accept(focusValue)
        }

        override fun longClick() {
            longClicksRelay.accept(Any())
        }
    }
}
