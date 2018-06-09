package com.lyft.domic.samples.shared.signin

import com.lyft.domic.samples.shared.signin.SignInService.SignInResult
import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit.SECONDS

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
