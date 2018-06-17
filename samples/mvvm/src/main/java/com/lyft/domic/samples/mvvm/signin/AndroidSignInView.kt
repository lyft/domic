package com.lyft.domic.samples.mvvm.signin

import android.view.ViewGroup
import com.lyft.domic.android.AndroidButton
import com.lyft.domic.android.AndroidEditText
import com.lyft.domic.android.AndroidTextView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.samples.mvvm.R

class AndroidSignInView(root: ViewGroup, renderer: Renderer) : SignInView {
    override val emailEditText by lazy { AndroidEditText(root.findViewById(R.id.email_edit_text), renderer) }
    override val passwordEditText by lazy { AndroidEditText(root.findViewById(R.id.password_edit_text), renderer) }
    override val signInButton by lazy { AndroidButton(root.findViewById(R.id.sign_in_button), renderer) }
    override val resultTextView by lazy { AndroidTextView(root.findViewById(R.id.sign_result_text_view), renderer) }
}
