package com.lyft.domic.samples.performance.domic

import com.lyft.domic.api.subscribe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit.MICROSECONDS
import java.util.concurrent.TimeUnit.MILLISECONDS

class DomicViewModel(view: DomicView) : Disposable {

    private val disposable = CompositeDisposable()

    init {
        disposable += Observable
                .interval(999, MICROSECONDS)
                .map { "Counter0: $it" }
                .subscribe(view.counter0.change::text)

        disposable += Observable
                .interval(1, MILLISECONDS)
                .map { "Counter1: $it" }
                .subscribe(view.counter1.change::text)

        disposable += Observable
                .interval(2, MILLISECONDS)
                .map { "Counter2: $it" }
                .subscribe(view.counter2.change::text)

        disposable += Observable
                .interval(3, MILLISECONDS)
                .map { "Counter3: $it" }
                .subscribe(view.counter3.change::text)

        disposable += Observable
                .interval(4, MILLISECONDS)
                .map { "Counter4: $it" }
                .subscribe(view.counter4.change::text)

        disposable += Observable
                .interval(5, MILLISECONDS)
                .map { "Counter5: $it" }
                .subscribe(view.counter5.change::text)

        disposable += Observable
                .interval(6, MILLISECONDS)
                .map { "Counter6: $it" }
                .subscribe(view.counter6.change::text)

        disposable += Observable
                .interval(7, MILLISECONDS)
                .map { "Counter7: $it" }
                .subscribe(view.counter7.change::text)

        disposable += Observable
                .interval(8, MILLISECONDS)
                .map { "Counter8: $it" }
                .subscribe(view.counter8.change::text)

        disposable += Observable
                .interval(9, MILLISECONDS)
                .map { "Counter9: $it" }
                .subscribe(view.counter9.change::text)

        disposable += Observable
                .interval(4, MILLISECONDS)
                .map { it % 2L == 0L }
                .subscribe(view.button.change::enabled)

        disposable += Observable
                .interval(4, MILLISECONDS)
                .map { it % 2L == 0L }
                .subscribe(view.checkBox.change::checked)

        disposable += Observable
                .interval(4, MILLISECONDS)
                .map { it % 2L == 0L }
                .subscribe(view.radioButton.change::checked)
    }


    override fun isDisposed() = disposable.isDisposed

    override fun dispose() = disposable.dispose()
}
