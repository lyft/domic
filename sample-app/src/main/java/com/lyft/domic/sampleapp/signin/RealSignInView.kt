package com.lyft.domic.sampleapp.signin

import android.view.ViewGroup
import com.lyft.domic.sampleapp.R
import com.lyft.domic.android.AndroidButton
import com.lyft.domic.android.AndroidEditText
import com.lyft.domic.android.AndroidTextView

class RealSignInView(root: ViewGroup) : SignInView {
    override val emailEditText by lazy { AndroidEditText(root.findViewById(R.id.email_edit_text)) }
    override val passwordEditText by lazy { AndroidEditText(root.findViewById(R.id.password_edit_text)) }
    override val signInButton by lazy { AndroidButton(root.findViewById(R.id.sign_in_button)) }
    override val resultTextView by lazy { AndroidTextView(root.findViewById(R.id.sign_result_text_view)) }
}
