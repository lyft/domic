package com.lyft.domic.samples.mvvm.signin

import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.gojuno.koptional.rxjava2.filterSome
import com.jakewharton.rx.replayingShare
import com.lyft.domic.api.subscribe
import com.lyft.domic.samples.shared.signin.SignInService
import com.lyft.domic.samples.shared.signin.SignInService.Credentials
import com.lyft.domic.samples.shared.signin.SignInService.SignInResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom

class SignInViewModel(view: SignInView, service: SignInService): Disposable {

    private val disposable = CompositeDisposable()

    init {
        val credentials = Observables
                .combineLatest(
                        view.emailEditText.observe.textChanges,
                        view.passwordEditText.observe.textChanges,
                        { email, password -> email to password }
                )
                .map {
                    val valid = it.first.trim().isNotEmpty() && it.second.trim().isNotEmpty()

                    when (valid) {
                        true -> Some(Credentials(it.first.toString(), it.second.toString()))
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
                .subscribe(view.signInButton.change::enabled)

        disposable += credentials
                .map { "" }
                .subscribe(view.resultTextView.change::text)

        val signInRequest = view.signInButton
                .observe
                .clicks
                .withLatestFrom(credentials) { _, creds -> creds }
                .filterSome()
                .replayingShare()

        disposable += signInRequest
                .map { false }
                .subscribe(view.signInButton.change::enabled)

        disposable += signInRequest
                .map { "Signing in…" }
                .subscribe(view.resultTextView.change::text)

        val signInResult = signInRequest
                .switchMap { service.signIn(it) }
                .replayingShare()

        disposable += signInResult
                .map {
                    when (it) {
                        is SignInResult.Success -> "Successfully signed in!"
                        is SignInResult.Error -> "Couldn't sign in because ${it.cause.message}"
                    }
                }
                .subscribe(view.resultTextView.change::text)

        disposable += signInResult
                .map { true }
                .subscribe(view.signInButton.change::enabled)
    }

    override fun isDisposed() = disposable.isDisposed

    override fun dispose() = disposable.dispose()
}

