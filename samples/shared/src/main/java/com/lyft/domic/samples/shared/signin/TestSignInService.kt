package com.lyft.domic.samples.shared.signin

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.observers.TestObserver

class TestSignInService : SignInService {

        val signInCredentialsRelay = PublishRelay.create<SignInService.Credentials>()
        val signInCredentialsObserver = TestObserver<SignInService.Credentials>()
        val signIn = PublishRelay.create<SignInService.SignInResult>()

        init {
            signInCredentialsRelay.subscribe(signInCredentialsObserver)
        }

        override fun signIn(credentials: SignInService.Credentials): Observable<SignInService.SignInResult> {
            signInCredentialsRelay.accept(credentials)
            return signIn
        }
    }
