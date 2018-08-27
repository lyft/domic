package com.lyft.domic.samples.redux.rxredux.signin

import android.view.ViewGroup
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.PublishRelay
import com.lyft.domic.android.AndroidButton
import com.lyft.domic.android.AndroidEditText
import com.lyft.domic.android.AndroidTextView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.samples.redux.rxredux.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class AndroidSignInView(root: ViewGroup, renderer: Renderer) : SignInView {

    private val emailEditText = AndroidEditText(root.findViewById(R.id.email_edit_text), renderer)
    private val passwordEditText = AndroidEditText(root.findViewById(R.id.password_edit_text), renderer)
    private val signInButton = AndroidButton(root.findViewById(R.id.sign_in_button), renderer)
    private val resultTextView = AndroidTextView(root.findViewById(R.id.sign_result_text_view), renderer)

    override val actions: Observable<SignInAction> = Observable
            .create<SignInAction> { emitter ->
                val disposable = CompositeDisposable()
                emitter.setDisposable(disposable)

                disposable += emailEditText
                        .observe
                        .textChanges
                        .map { SignInAction.ChangeEmail(it) }
                        .subscribe { emitter.onNext(it) }

                disposable += passwordEditText
                        .observe
                        .textChanges
                        .map { SignInAction.ChangePassword(it) }
                        .subscribe { emitter.onNext(it) }

                disposable += signInButton
                        .observe
                        .clicks
                        .map { SignInAction.SignIn }
                        .subscribe { emitter.onNext(it) }
            }
            .share()

    override fun render(signInState: Observable<SignInState>): Disposable {
        val state = signInState
                .replayingShare()
                .observeOn(Schedulers.computation())

        val disposable = CompositeDisposable()

        disposable += state
                .map { it.signInButtonEnabled }
                .subscribe(signInButton.change::enabled)

        disposable += state
                .map {
                    when (it) {
                        is SignInState.Idle -> ""
                        is SignInState.SigningIn -> "Signing in…"
                        is SignInState.SignInSuccessful -> "Successfully signed in!"
                        is SignInState.SignInFailed -> "Couldn't sign in because ${it.cause.message}"
                    }
                }
                .subscribe(resultTextView.change::text)

        return disposable
    }
}
