package com.lyft.domic.samples.performance.regular

import com.lyft.domic.api.subscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit.MICROSECONDS
import java.util.concurrent.TimeUnit.MILLISECONDS

class RegularPresenter(view: RegularView) : Disposable {

    private val disposable = CompositeDisposable()

    init {
        disposable += Observable
                .interval(999, MICROSECONDS)
                .map { "Counter0: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter0Text)

        disposable += Observable
                .interval(1, MILLISECONDS)
                .map { "Counter1: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter1Text)

        disposable += Observable
                .interval(2, MILLISECONDS)
                .map { "Counter2: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter2Text)

        disposable += Observable
                .interval(3, MILLISECONDS)
                .map { "Counter3: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter3Text)

        disposable += Observable
                .interval(4, MILLISECONDS)
                .map { "Counter4: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter4Text)

        disposable += Observable
                .interval(5, MILLISECONDS)
                .map { "Counter5: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter5Text)

        disposable += Observable
                .interval(6, MILLISECONDS)
                .map { "Counter6: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter6Text)

        disposable += Observable
                .interval(7, MILLISECONDS)
                .map { "Counter7: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter7Text)

        disposable += Observable
                .interval(8, MILLISECONDS)
                .map { "Counter8: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter8Text)

        disposable += Observable
                .interval(9, MILLISECONDS)
                .map { "Counter9: $it" }
                .observeOn(mainThread())
                .subscribe(view::setCounter9Text)

        disposable += Observable
                .interval(4, MILLISECONDS)
                .map { it % 2L == 0L }
                .observeOn(mainThread())
                .subscribe(view::setButtonEnabled)

        disposable += Observable
                .interval(4, MILLISECONDS)
                .map { it % 2L == 0L }
                .observeOn(mainThread())
                .subscribe(view::setCheckBoxChecked)

        disposable += Observable
                .interval(4, MILLISECONDS)
                .map { it % 2L == 0L }
                .observeOn(mainThread())
                .subscribe(view::setRadioButtonChecked)
    }


    override fun isDisposed() = disposable.isDisposed

    override fun dispose() = disposable.dispose()
}
