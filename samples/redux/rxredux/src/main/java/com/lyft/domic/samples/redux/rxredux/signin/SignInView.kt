package com.lyft.domic.samples.redux.rxredux.signin

import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine.Action
import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine.State
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface SignInView {
    val actions: Observable<Action>
    fun render(signInState: Observable<State>): Disposable
}
