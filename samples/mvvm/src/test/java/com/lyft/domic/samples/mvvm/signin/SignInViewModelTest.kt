package com.lyft.domic.samples.mvvm.signin

import com.lyft.domic.samples.shared.signin.SignInService.Credentials
import com.lyft.domic.samples.shared.signin.SignInService.SignInResult
import com.lyft.domic.samples.shared.signin.TestSignInService
import com.lyft.domic.test.TestButton
import com.lyft.domic.test.TestEditText
import com.lyft.domic.test.TestTextView
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SignInViewModelTest {

    private val view = TestSignInView()
    private val service = TestSignInService()

    init {
        // Just instantiate, view controller can actually be converted into a function.
        SignInViewModel(view, service)
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

        service.signInCredentialsObserver.assertValue(Credentials(email = "test@email", password = "passw0rd"))
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
}
