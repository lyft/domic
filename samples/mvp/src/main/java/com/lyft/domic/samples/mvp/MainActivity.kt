package com.lyft.domic.samples.mvp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lyft.domic.android.rendering.AndroidRenderer
import com.lyft.domic.samples.mvp.signin.AndroidSignInView
import com.lyft.domic.samples.mvp.signin.SignInPresenter
import com.lyft.domic.samples.shared.signin.RealSignInService
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers.computation

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = AndroidSignInView(findViewById(android.R.id.content), AndroidRenderer.getInstance())

        disposable += Single
                .fromCallable { SignInPresenter(view, RealSignInService()) }
                // Showcase that you can observe Virtual DOM on non-main thread.
                .subscribeOn(computation())
                .subscribe { presenter -> disposable += presenter }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
