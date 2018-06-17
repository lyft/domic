package com.lyft.domic.samples.mvp.signin

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface SignInView {
    fun observeEmail(): Observable<out CharSequence>
    fun observePassword(): Observable<out CharSequence>
    fun observeSingInSubmitActions(): Observable<Any>

    fun changeSignInEnable(enabledValues: Observable<Boolean>): Disposable
    fun changeResultText(textValues: Observable<String>): Disposable
}
