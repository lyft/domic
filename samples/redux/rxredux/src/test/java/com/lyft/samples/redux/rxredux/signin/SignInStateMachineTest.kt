package com.lyft.samples.redux.rxredux.signin

import com.jakewharton.rxrelay2.PublishRelay
import com.lyft.domic.samples.redux.rxredux.signin.SignInAction
import com.lyft.domic.samples.redux.rxredux.signin.SignInState
import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine
import com.lyft.domic.samples.shared.signin.SignInService.Credentials
import com.lyft.domic.samples.shared.signin.SignInService.SignInResult
import com.lyft.domic.samples.shared.signin.TestSignInService
import com.lyft.domic.test.TestButton
import com.lyft.domic.test.TestEditText
import com.lyft.domic.test.TestTextView
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SignInStateMachineTest {

    private val inputActions = PublishRelay.create<SignInAction>()
    private val service = TestSignInService()

    private val stateObserver = SignInStateMachine(inputActions, service, Schedulers.trampoline())
            .state
            .test()

    @Test
    fun `sign in button disabled by default`() {
        assertThat(stateObserver.values().first().signInButtonEnabled).isFalse()
    }

    @Test
    fun `sign in button enabled if valid credentials entered`() {
        inputActions.accept(SignInAction.ChangeEmail("test@email"))
        inputActions.accept(SignInAction.ChangePassword("passw0rd"))

        assertThat(stateObserver.values().last().signInButtonEnabled).isTrue()
    }

    @Test
    fun `disables button after click with credentials`() {
        inputActions.accept(SignInAction.ChangeEmail("test@email"))
        inputActions.accept(SignInAction.ChangePassword("passw0rd"))

        inputActions.accept(SignInAction.SignIn)

        assertThat(stateObserver.values().last().signInButtonEnabled).isFalse()
    }

    @Test
    fun `sends request with valid credentials`() {
        inputActions.accept(SignInAction.ChangeEmail("test@email"))
        inputActions.accept(SignInAction.ChangePassword("passw0rd"))

        inputActions.accept(SignInAction.SignIn)

        service.signInCredentialsObserver.assertValue(Credentials(email = "test@email", password = "passw0rd"))
    }

    @Test
    fun `displays success ui`() {
        inputActions.accept(SignInAction.ChangeEmail("test@email"))
        inputActions.accept(SignInAction.ChangePassword("passw0rd"))

        inputActions.accept(SignInAction.SignIn)

        service.signIn.accept(SignInResult.Success)

        assertThat(stateObserver.values().last()).isExactlyInstanceOf(SignInState.SignInSuccessful::class.java)
    }

    @Test
    fun `displays failure ui`() {
        inputActions.accept(SignInAction.ChangeEmail("test@email"))
        inputActions.accept(SignInAction.ChangePassword("passw0rd"))

        inputActions.accept(SignInAction.SignIn)

        service.signIn.accept(SignInResult.Error(cause = Exception()))

        assertThat(stateObserver.values().last()).isExactlyInstanceOf(SignInState.SignInFailed::class.java)
    }
}
