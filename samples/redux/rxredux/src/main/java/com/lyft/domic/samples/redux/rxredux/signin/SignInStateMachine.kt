package com.lyft.domic.samples.redux.rxredux.signin

import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.lyft.domic.samples.shared.signin.SignInService
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.ofType

class SignInStateMachine(
        inputActions: Observable<SignInAction>,
        private val signInService: SignInService,
        computationScheduler: Scheduler
) {

    val state: Observable<SignInState> = inputActions
            .observeOn(computationScheduler)
            .reduxStore(
                    initialState = SignInState.Idle(email = "", password = "", signInButtonEnabled = false),
                    sideEffects = listOf(::signInSideEffect),
                    reducer = ::reducer
            )
            .distinctUntilChanged()

    private fun reducer(state: SignInState, action: SignInAction): SignInState = when (action) {
        is SignInAction.ChangeEmail -> {
            val email = action.email.trim()
            val password = state.password

            val signInButtonEnabled = validateEmailAndPassword(email, password)

            SignInState.Idle(
                    email = email,
                    password = password,
                    signInButtonEnabled = signInButtonEnabled
            )
        }
        is SignInAction.ChangePassword -> {
            val email = state.email
            val password = action.password.trim()

            val signInButtonEnabled = validateEmailAndPassword(email, password)

            SignInState.Idle(
                    email = state.email,
                    password = action.password,
                    signInButtonEnabled = signInButtonEnabled
            )
        }
        is SignInAction.SignIn -> SignInState.SigningIn(
                email = state.email,
                password = state.password,
                signInButtonEnabled = false
        )
        is SignInAction.ShowSigningInUi -> SignInState.SigningIn(
                email = state.email,
                password = state.password,
                signInButtonEnabled = false
        )
        is SignInAction.ShowSignInSuccessfulUi -> SignInState.SignInSuccessful(
                email = state.email,
                password = state.password,
                signInButtonEnabled = true
        )
        is SignInAction.ShowSignInFailureUi -> SignInState.SignInFailed(
                email = state.email,
                password = state.password,
                cause = action.cause,
                signInButtonEnabled = true
        )
    }

    private fun validateEmailAndPassword(email: CharSequence, password: CharSequence): Boolean = email.isNotEmpty() && password.isNotEmpty()

    private fun signInSideEffect(actions: Observable<SignInAction>, state: StateAccessor<SignInState>): Observable<SignInAction> = actions
            .ofType<SignInAction.SignIn>()
            .map { state() }
            .map { SignInService.Credentials(email = it.email, password = it.password) }
            .switchMap { credentials ->
                signInService
                        .signIn(credentials)
                        .map { result ->
                            when (result) {
                                is SignInService.SignInResult.Success -> SignInAction.ShowSignInSuccessfulUi
                                is SignInService.SignInResult.Error -> SignInAction.ShowSignInFailureUi(result.cause)
                            }
                        }
                        .startWith(SignInAction.ShowSigningInUi)
            }


}
