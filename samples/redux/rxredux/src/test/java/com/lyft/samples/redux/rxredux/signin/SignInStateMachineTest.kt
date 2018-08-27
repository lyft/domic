package com.lyft.samples.redux.rxredux.signin

import com.jakewharton.rxrelay2.PublishRelay
import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine
import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine.Action
import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine.State
import com.lyft.domic.samples.shared.signin.SignInService.Credentials
import com.lyft.domic.samples.shared.signin.SignInService.SignInResult
import com.lyft.domic.samples.shared.signin.TestSignInService
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SignInStateMachineTest {

    private val inputActions = PublishRelay.create<Action>()
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
        inputActions.accept(Action.ChangeEmail("test@email"))
        inputActions.accept(Action.ChangePassword("passw0rd"))

        assertThat(stateObserver.values().last().signInButtonEnabled).isTrue()
    }

    @Test
    fun `disables button after click with credentials`() {
        inputActions.accept(Action.ChangeEmail("test@email"))
        inputActions.accept(Action.ChangePassword("passw0rd"))

        inputActions.accept(Action.SignIn)

        assertThat(stateObserver.values().last().signInButtonEnabled).isFalse()
    }

    @Test
    fun `sends request with valid credentials`() {
        inputActions.accept(Action.ChangeEmail("test@email"))
        inputActions.accept(Action.ChangePassword("passw0rd"))

        inputActions.accept(Action.SignIn)

        service.signInCredentialsObserver.assertValue(Credentials(email = "test@email", password = "passw0rd"))
    }

    @Test
    fun `displays success ui`() {
        inputActions.accept(Action.ChangeEmail("test@email"))
        inputActions.accept(Action.ChangePassword("passw0rd"))

        inputActions.accept(Action.SignIn)

        service.signIn.accept(SignInResult.Success)

        assertThat(stateObserver.values().last()).isExactlyInstanceOf(State.SignInSuccessful::class.java)
    }

    @Test
    fun `displays failure ui`() {
        inputActions.accept(Action.ChangeEmail("test@email"))
        inputActions.accept(Action.ChangePassword("passw0rd"))

        inputActions.accept(Action.SignIn)

        service.signIn.accept(SignInResult.Error(cause = Exception()))

        assertThat(stateObserver.values().last()).isExactlyInstanceOf(State.SignInFailed::class.java)
    }
}
