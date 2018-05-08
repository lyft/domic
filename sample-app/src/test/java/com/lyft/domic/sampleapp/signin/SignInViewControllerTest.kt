package com.lyft.domic.sampleapp.signin

import com.jakewharton.rxrelay2.PublishRelay
import com.lyft.domic.sampleapp.signin.SignInService.*
import com.lyft.domic.test.TestButton
import com.lyft.domic.test.TestEditText
import com.lyft.domic.test.TestTextView
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.*
import org.junit.Test

class SignInViewControllerTest {

    private val view = TestSignInView()
    private val service = spy(TestSignInService())

    init {
        // Just instantiate, view controller can actually be converted into a function.
        SignInViewController(view, service)
    }

    @Test
    fun `sign in button disabled by default`() {
        assertThat(view.signInButton.check.enabled).isFalse()
    }

    @Test
    fun `sign in button enabled if valid credentials entered`() {
        view.emailEditText.simulate.text("some@email.com")
        view.passwordEditText.simulate.text("password")

        assertThat(view.signInButton.check.enabled).isTrue()
    }

    @Test
    fun `disables button after click with credentials`() {
        view.emailEditText.simulate.text("test@email")
        view.passwordEditText.simulate.text("passw0rd")

        view.signInButton.simulate.click()

        assertThat(view.signInButton.check.enabled).isFalse()
    }

    @Test
    fun `sends request with valid credentials`() {
        view.emailEditText.simulate.text("test@email")
        view.passwordEditText.simulate.text("passw0rd")

        view.signInButton.simulate.click()

        verify(service).signIn(Credentials(email = "test@email", password = "passw0rd"))
    }

    @Test
    fun `displays success message`() {
        view.emailEditText.simulate.text("test@email")
        view.passwordEditText.simulate.text("passw0rd")

        view.signInButton.simulate.click()

        service.signIn.accept(SignInResult.Success)

        assertThat(view.resultTextView.check.text).isEqualTo("Successfully signed in!")
    }

    class TestSignInView : SignInView {
        override val emailEditText = TestEditText()
        override val passwordEditText = TestEditText()
        override val signInButton = TestButton()
        override val resultTextView = TestTextView()
    }

    open class TestSignInService : SignInService {

        val signIn = PublishRelay.create<SignInService.SignInResult>()

        override fun signIn(credentials: SignInService.Credentials) = signIn
    }
}
