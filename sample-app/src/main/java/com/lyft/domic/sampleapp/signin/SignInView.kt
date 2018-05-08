package com.lyft.domic.sampleapp.signin

import com.lyft.domic.api.Button
import com.lyft.domic.api.EditText
import com.lyft.domic.api.TextView

interface SignInView { // MVP, MVVM
    val emailEditText: EditText
    val passwordEditText: EditText
    val signInButton: Button
    val resultTextView: TextView
}


interface SignInReadOnlyView {
    val emailEditText: EditText.Observe
    val passwordEditText: EditText.Observe
    val signInButton: Button.Observe
    val resultTextView: TextView.Observe
}

interface SignInWritableView {
    val emailEditText: EditText.Change
    val passwordEditText: EditText.Change
    val signInButton: Button.Change
    val resultTextView: TextView.Change
}
