package com.lyft.domic.samples.redux.rxredux.signin

sealed class SignInState(
        open val email: CharSequence,
        open val password: CharSequence,
        open val signInButtonEnabled: Boolean
) {

    data class Idle(
            override val email: CharSequence,
            override val password: CharSequence,
            override val signInButtonEnabled: Boolean
    ) : SignInState(email, password, signInButtonEnabled)

    data class SigningIn(
            override val email: CharSequence,
            override val password: CharSequence,
            override val signInButtonEnabled: Boolean
    ) : SignInState(email, password, signInButtonEnabled)

    data class SignInSuccessful(
            override val email: CharSequence,
            override val password: CharSequence,
            override val signInButtonEnabled: Boolean
    ) : SignInState(email, password, signInButtonEnabled)

    data class SignInFailed(
            override val email: CharSequence,
            override val password: CharSequence,
            override val signInButtonEnabled: Boolean,
            val cause: Throwable
    ) : SignInState(email, password, signInButtonEnabled)
}
