package com.lyft.domic.samples.mvp.signin

import android.view.ViewGroup
import com.lyft.domic.android.AndroidButton
import com.lyft.domic.android.AndroidEditText
import com.lyft.domic.android.AndroidTextView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.samples.mvp.R
import io.reactivex.Observable

class AndroidSignInView(root: ViewGroup, renderer: Renderer) : SignInView {
    private val emailEditText by lazy { AndroidEditText(root.findViewById(R.id.email_edit_text), renderer) }
    private val passwordEditText by lazy { AndroidEditText(root.findViewById(R.id.password_edit_text), renderer) }
    private val signInButton by lazy { AndroidButton(root.findViewById(R.id.sign_in_button), renderer) }
    private val resultTextView by lazy { AndroidTextView(root.findViewById(R.id.sign_result_text_view), renderer) }

    override fun observeEmail() = emailEditText.observe.textChanges
    override fun observePassword() = passwordEditText.observe.textChanges
    override fun observeSingInSubmitActions() = signInButton.observe.clicks

    override fun changeSignInEnable(enabledValues: Observable<Boolean>) = enabledValues.subscribe(signInButton.change::enabled)
    override fun changeResultText(textValues: Observable<String>) = textValues.subscribe(resultTextView.change::text)
}
