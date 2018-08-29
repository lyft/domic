package com.lyft.domic.samples.redux.rxredux.signin

import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.lyft.domic.samples.shared.signin.SignInService
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.ofType

class SignInStateMachine(
        inputActions: Observable<Action>,
        private val signInService: SignInService,
        computationScheduler: Scheduler
) {

    sealed class Action {
        data class ChangeEmail(val email: CharSequence) : Action()
        data class ChangePassword(val password: CharSequence) : Action()
        object SignIn : Action()
        object SigningIn : Action()
        object SignInSuccessful : Action()
        data class SignInFailed(val cause: Throwable) : Action()
    }

    sealed class State {

        abstract val email: CharSequence
        abstract val password: CharSequence
        abstract val signInButtonEnabled: Boolean

        data class Idle(
                override val email: CharSequence,
                override val password: CharSequence,
                override val signInButtonEnabled: Boolean
        ) : State()

        data class SigningIn(
                override val email: CharSequence,
                override val password: CharSequence,
                override val signInButtonEnabled: Boolean
        ) : State()

        data class SignInSuccessful(
                override val email: CharSequence,
                override val password: CharSequence,
                override val signInButtonEnabled: Boolean
        ) : State()

        data class SignInFailed(
                override val email: CharSequence,
                override val password: CharSequence,
                override val signInButtonEnabled: Boolean,
                val cause: Throwable
        ) : State()
    }


    val state: Observable<State> = inputActions
            .observeOn(computationScheduler)
            .reduxStore(
                    initialState = State.Idle(email = "", password = "", signInButtonEnabled = false),
                    sideEffects = listOf(::signInSideEffect),
                    reducer = ::reducer
            )
            .distinctUntilChanged()

    private fun reducer(state: State, action: Action): State = when (action) {
        is Action.ChangeEmail -> {
            val email = action.email.trim()
            val password = state.password

            val signInButtonEnabled = validateEmailAndPassword(email, password)

            State.Idle(
                    email = email,
                    password = password,
                    signInButtonEnabled = signInButtonEnabled
            )
        }
        is Action.ChangePassword -> {
            val email = state.email
            val password = action.password.trim()

            val signInButtonEnabled = validateEmailAndPassword(email, password)

            State.Idle(
                    email = state.email,
                    password = action.password,
                    signInButtonEnabled = signInButtonEnabled
            )
        }
        is Action.SignIn, is Action.SigningIn -> State.SigningIn(
                email = state.email,
                password = state.password,
                signInButtonEnabled = false
        )
        is Action.SignInSuccessful -> State.SignInSuccessful(
                email = state.email,
                password = state.password,
                signInButtonEnabled = true
        )
        is Action.SignInFailed -> State.SignInFailed(
                email = state.email,
                password = state.password,
                cause = action.cause,
                signInButtonEnabled = true
        )
    }

    private fun validateEmailAndPassword(email: CharSequence, password: CharSequence): Boolean = email.isNotEmpty() && password.isNotEmpty()

    private fun signInSideEffect(actions: Observable<Action>, state: StateAccessor<State>): Observable<Action> = actions
            .ofType<Action.SignIn>()
            .map { state() }
            .map { SignInService.Credentials(email = it.email, password = it.password) }
            .switchMap { credentials ->
                signInService
                        .signIn(credentials)
                        .map { result ->
                            when (result) {
                                is SignInService.SignInResult.Success -> Action.SignInSuccessful
                                is SignInService.SignInResult.Error -> Action.SignInFailed(result.cause)
                            }
                        }
            }
}
