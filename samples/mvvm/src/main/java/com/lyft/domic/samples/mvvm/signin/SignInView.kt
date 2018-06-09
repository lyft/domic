package com.lyft.domic.samples.mvvm.signin

import com.lyft.domic.api.Button
import com.lyft.domic.api.EditText
import com.lyft.domic.api.TextView

interface SignInView {
    val emailEditText: EditText
    val passwordEditText: EditText
    val signInButton: Button
    val resultTextView: TextView
}
