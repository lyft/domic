package com.lyft.domic.samples.mvvm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lyft.domic.android.rendering.AndroidRenderer
import com.lyft.domic.samples.shared.signin.RealSignInService
import com.lyft.domic.samples.mvvm.signin.AndroidSignInView
import com.lyft.domic.samples.mvvm.signin.SignInViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = AndroidSignInView(findViewById(android.R.id.content), AndroidRenderer.getInstance())

        disposable += Single
                .fromCallable { SignInViewModel(view, RealSignInService()) }
                // Showcase that you can observe Virtual DOM on non-main thread.
                .subscribeOn(Schedulers.computation())
                .subscribe { viewModel -> disposable += viewModel }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
