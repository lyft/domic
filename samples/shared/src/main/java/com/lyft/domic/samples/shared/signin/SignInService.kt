package com.lyft.domic.samples.shared.signin

import io.reactivex.Observable

interface SignInService {

    data class Credentials(val email: CharSequence, val password: CharSequence)

    sealed class SignInResult {
        object Success : SignInResult()
        data class Error(val cause: Throwable) : SignInResult()
    }

    fun signIn(credentials: Credentials): Observable<SignInResult>
}
