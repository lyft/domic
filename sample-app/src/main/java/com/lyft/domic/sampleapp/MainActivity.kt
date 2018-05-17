package com.lyft.domic.sampleapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lyft.domic.android.rendering.AndroidRenderer
import com.lyft.domic.sampleapp.signin.RealSignInService
import com.lyft.domic.sampleapp.signin.RealSignInView
import com.lyft.domic.sampleapp.signin.SignInViewController
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = RealSignInView(findViewById(android.R.id.content), AndroidRenderer.getInstance())

        disposable += Single
                .fromCallable { SignInViewController(view, RealSignInService()) }
                // Showcase that you can observe Virtual DOM on non-main thread.
                .subscribeOn(Schedulers.computation())
                .subscribe { viewController -> disposable += viewController }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
