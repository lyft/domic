package com.lyft.domic.samples.mvp.signin

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.lyft.domic.samples.shared.signin.SignInService
import com.lyft.domic.samples.shared.signin.SignInService.*
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.*
import org.junit.Test

class SignInPresenterTest {

    private val view = TestSignInView()
    private val service = spy(TestSignInService())

    init {
        // Just instantiate, view controller can actually be converted into a function.
        SignInPresenter(view, service)
    }

    @Test
    fun `sign in button disabled by default`() {
        view.changeSignInEnableObserver.assertValue(false)
    }

    @Test
    fun `sign in button enabled if valid credentials entered`() {
        view.emailRelay.accept("some@email.com")
        view.passwordRelay.accept("password")

        assertThat(view.changeSignInEnableObserver.values().last()).isTrue()
    }

    @Test
    fun `disables button after submit action with credentials`() {
        view.emailRelay.accept("test@email")
        view.passwordRelay.accept("passw0rd")

        view.signInSubmitActionsRelay.accept(Unit)

        assertThat(view.changeSignInEnableObserver.values().last()).isFalse()
    }

    @Test
    fun `sends request with valid credentials`() {
        view.emailRelay.accept("test@email")
        view.passwordRelay.accept("passw0rd")

        view.signInSubmitActionsRelay.accept(Unit)

        verify(service).signIn(Credentials(email = "test@email", password = "passw0rd"))
    }

    @Test
    fun `displays success message`() {
        view.emailRelay.accept("test@email")
        view.passwordRelay.accept("passw0rd")

        view.signInSubmitActionsRelay.accept(Unit)

        service.signIn.accept(SignInResult.Success)

        assertThat(view.changeResultTextObserver.values().last()).isEqualTo("Successfully signed in!")
    }

    class TestSignInView : SignInView {

        val emailRelay: Relay<String> = PublishRelay.create()
        val passwordRelay: Relay<String> = PublishRelay.create()
        val signInSubmitActionsRelay: Relay<Any> = PublishRelay.create()

        private val changeSignInEnableRelay: Relay<Boolean> = PublishRelay.create()
        private val changeResultTextRelay: Relay<String> = PublishRelay.create()
        val changeSignInEnableObserver = TestObserver<Boolean>()
        val changeResultTextObserver = TestObserver<String>()

        init {
            changeSignInEnableRelay.subscribe(changeSignInEnableObserver)
            changeResultTextRelay.subscribe(changeResultTextObserver)
        }

        override fun observeEmail() = emailRelay
        override fun observePassword() = passwordRelay
        override fun observeSingInSubmitActions() = signInSubmitActionsRelay

        override fun changeSignInEnable(enabledValues: Observable<Boolean>) = enabledValues.subscribe(changeSignInEnableRelay)
        override fun changeResultText(textValues: Observable<String>) = textValues.subscribe(changeResultTextRelay)
    }

    open class TestSignInService : SignInService {

        val signIn = PublishRelay.create<SignInService.SignInResult>()

        override fun signIn(credentials: SignInService.Credentials) = signIn
    }
}
