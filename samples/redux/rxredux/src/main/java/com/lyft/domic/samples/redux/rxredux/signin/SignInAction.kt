package com.lyft.domic.samples.redux.rxredux.signin

sealed class SignInAction {
    data class ChangeEmail(val email: CharSequence) : SignInAction()
    data class ChangePassword(val password: CharSequence) : SignInAction()
    object SignIn : SignInAction()
    object ShowSigningInUi : SignInAction()
    object ShowSignInSuccessfulUi : SignInAction()
    data class ShowSignInFailureUi(val cause: Throwable) : SignInAction()
}
