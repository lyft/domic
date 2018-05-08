package com.lyft.domic.sampleapp.signin

import com.lyft.domic.sampleapp.signin.SignInService.*
import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit.*

class RealSignInService : SignInService {
    override fun signIn(credentials: SignInService.Credentials) = Observable
            .fromCallable { Random().nextBoolean() }
            .delay { Observable.timer(Random().nextInt(5).toLong(), SECONDS) }
            .map {
                when (it) {
                    true -> SignInResult.Success
                    false -> SignInResult.Error(cause = Exception("some network problem"))
                }
            }
}
