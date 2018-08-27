package com.lyft.domic.test

import com.lyft.domic.api.TextView
import com.lyft.domic.api.View
import com.lyft.domic.util.sharedDistinctUntilChanged
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReferenceArray

class TestTextView : TextView {

    interface Check : TestView.Check {

        val text: CharSequence?
    }

    interface Simulate : TestView.Simulate {

        fun <T : CharSequence> text(text: T)
    }

    private val asTestView = TestView()

    private val textChangesRelay = TestPublishRelay.create<CharSequence>()
    private val textChangeEventsRelay = TestPublishRelay.create<Any>()

    override val observe: TextView.Observe = object : TextView.Observe, View.Observe by asTestView.observe {

        override val textChanges: Observable<out CharSequence> = textChangesRelay

        override val textChangeEvents: Observable<Any> = textChangeEventsRelay
    }

    override val change: TextView.Change = object : TextView.Change, View.Change by asTestView.change {

        private val state = AtomicReferenceArray<Any>(1)

        override fun text(textValues: Observable<out CharSequence>): Disposable {
            return textValues
                    .sharedDistinctUntilChanged(state, 0)
                    .subscribe { text ->
                        textChangesRelay.accept(text)
                        textChangeEventsRelay.accept(Any())
                    }
        }
    }

    val check: Check = object : Check, TestView.Check by asTestView.check {

        override val text: CharSequence?
            get() = textChangesRelay.lastValue()
    }

    val simulate: Simulate = object : Simulate, TestView.Simulate by asTestView.simulate {

        override fun <T : CharSequence> text(text: T) {
            textChangesRelay.accept(text)
            textChangeEventsRelay.accept(Any())
        }
    }
}
