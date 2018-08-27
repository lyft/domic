package com.lyft.domic.test

import com.lyft.domic.api.Button
import com.lyft.domic.api.CompoundButton
import com.lyft.domic.util.sharedDistinctUntilChanged
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReferenceArray

class TestCompoundButton : CompoundButton {

    interface Check {
        val checked: Boolean?
    }

    interface Simulate {
        fun checked(checked: Boolean)
    }

    private val asTestButton = TestButton()

    private val checkedRelay = TestPublishRelay.create<Boolean>()

    override val observe: CompoundButton.Observe = object : CompoundButton.Observe, Button.Observe by asTestButton.observe {
        override val checked: Observable<out Boolean> = checkedRelay
    }

    override val change: CompoundButton.Change = object : CompoundButton.Change, Button.Change by asTestButton.change {

        private val state = AtomicReferenceArray<Any>(1)

        override fun checked(checkedValues: Observable<Boolean>): Disposable = checkedValues
                .sharedDistinctUntilChanged(state, 0)
                .subscribe(checkedRelay)
    }

    val check: Check = object : Check {
        override val checked: Boolean?
            get() = checkedRelay.lastValue()
    }

    val simulate: Simulate = object : Simulate {
        override fun checked(checked: Boolean) = checkedRelay.accept(checked)
    }
}
