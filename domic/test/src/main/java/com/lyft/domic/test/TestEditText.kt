package com.lyft.domic.test

import com.lyft.domic.api.EditText
import com.lyft.domic.api.TextView
import com.lyft.domic.util.sharedDistinctUntilChanged

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReferenceArray

class TestEditText : EditText {

    interface Check : TestTextView.Check {

        val selection: Int?
    }

    interface Simulate : TestTextView.Simulate {

        fun selection(selection: Int)
    }

    private val asTestTextView = TestTextView()

    private val selectionRelay = TestPublishRelay.create<Int>()

    override val observe: EditText.Observe = object : EditText.Observe, TextView.Observe by asTestTextView.observe {

    }

    override val change: EditText.Change = object : EditText.Change, TextView.Change by asTestTextView.change {

        private val state = AtomicReferenceArray<Any>(1)

        override fun selection(selectionValues: Observable<Int>): Disposable =
                selectionValues
                        .sharedDistinctUntilChanged(state, 0)
                        .subscribe(selectionRelay)
    }

    val check: Check = object : Check, TestTextView.Check by asTestTextView.check {

        override val selection: Int?
            get() = selectionRelay.lastValue()
    }

    val simulate: Simulate = object : Simulate, TestTextView.Simulate by asTestTextView.simulate {

        override fun selection(selection: Int) {
            selectionRelay.accept(selection)
        }
    }
}
