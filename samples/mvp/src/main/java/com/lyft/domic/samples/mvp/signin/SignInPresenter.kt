package com.lyft.domic.samples.mvp.signin

import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.gojuno.koptional.rxjava2.filterSome
import com.jakewharton.rx.replayingShare
import com.lyft.domic.api.subscribe
import com.lyft.domic.samples.shared.signin.SignInService
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom

class SignInPresenter(view: SignInView, signInService: SignInService): Disposable {

    private val disposable = CompositeDisposable()

    init {
        val credentials = Observables
                .combineLatest(
                        view.observeEmail(),
                        view.observePassword(),
                        { email, password -> email to password }
                )
                .map {
                    val valid = it.first.trim().isNotEmpty() && it.second.trim().isNotEmpty()

                    when (valid) {
                        true -> Some(SignInService.Credentials(it.first.toString(), it.second.toString()))
                        false -> None
                    }
                }
                .replayingShare()

        disposable += credentials
                .map {
                    when (it) {
                        is Some -> true
                        is None -> false
                    }
                }
                .startWith(false)
                .subscribe(view::changeSignInEnable)

        disposable += credentials
                .map { "" }
                .subscribe(view::changeResultText)

        val signInRequest = view
                .observeSingInSubmitActions()
                .withLatestFrom(credentials) { _, creds -> creds }
                .filterSome()
                .replayingShare()

        disposable += signInRequest
                .map { false }
                .subscribe(view::changeSignInEnable)

        disposable += signInRequest
                .map { "Signing in…" }
                .subscribe(view::changeResultText)

        val signInResult = signInRequest
                .switchMap { signInService.signIn(it) }
                .replayingShare()

        disposable += signInResult
                .map {
                    when (it) {
                        is SignInService.SignInResult.Success -> "Successfully signed in!"
                        is SignInService.SignInResult.Error -> "Couldn't sign in because ${it.cause.message}"
                    }
                }
                .subscribe(view::changeResultText)

        disposable += signInResult
                .map { true }
                .subscribe(view::changeSignInEnable)
    }

    override fun isDisposed() = disposable.isDisposed

    override fun dispose() = disposable.dispose()

}