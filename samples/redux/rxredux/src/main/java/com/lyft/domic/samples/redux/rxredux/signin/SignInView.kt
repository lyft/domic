package com.lyft.domic.samples.redux.rxredux.signin

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface SignInView {
    val actions: Observable<SignInAction>
    fun render(signInState: Observable<SignInState>): Disposable
}
