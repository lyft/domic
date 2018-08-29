package com.lyft.domic.samples.redux.rxredux.signin

import android.view.ViewGroup
import com.jakewharton.rx.replayingShare
import com.lyft.domic.android.AndroidButton
import com.lyft.domic.android.AndroidEditText
import com.lyft.domic.android.AndroidTextView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.samples.redux.rxredux.R
import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine.Action
import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine.State
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

    override val actions: Observable<Action> = Observable
            .merge(
                    emailEditText
                            .observe
                            .textChanges
                            .map { Action.ChangeEmail(it) },
                    passwordEditText
                            .observe
                            .textChanges
                            .map { Action.ChangePassword(it) },
                    signInButton
                            .observe
                            .clicks
                            .map { Action.SignIn }
            )
            .share()

    override fun render(signInState: Observable<State>): Disposable {
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
                        is State.Idle -> ""
                        is State.SigningIn -> "Signing in…"
                        is State.SignInSuccessful -> "Successfully signed in!"
                        is State.SignInFailed -> "Couldn't sign in because ${it.cause.message}"
                    }
                }
                .subscribe(resultTextView.change::text)

        return disposable
    }
}
