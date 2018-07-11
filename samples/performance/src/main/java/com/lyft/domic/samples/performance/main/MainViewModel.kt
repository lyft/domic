package com.lyft.domic.samples.performance.main

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign

class MainViewModel(view: MainView) : Disposable {

    private val disposable = CompositeDisposable()

    init {
        disposable += view.regularUiButton
                .observe
                .clicks
                .switchMapCompletable { view.navigateToRegular() }
                .subscribe()

        disposable += view.domicButton
                .observe
                .clicks
                .switchMapCompletable { view.navigateToDomic() }
                .subscribe()
    }

    override fun isDisposed() = disposable.isDisposed

    override fun dispose() = disposable.dispose()
}
