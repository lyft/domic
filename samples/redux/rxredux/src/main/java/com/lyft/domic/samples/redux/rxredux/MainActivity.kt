package com.lyft.domic.samples.redux.rxredux

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lyft.domic.android.rendering.AndroidRenderer
import com.lyft.domic.samples.shared.signin.RealSignInService
import com.lyft.domic.samples.redux.rxredux.signin.AndroidSignInView
import com.lyft.domic.samples.redux.rxredux.signin.SignInStateMachine
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val renderer = AndroidRenderer()
        val view = AndroidSignInView(findViewById(android.R.id.content), renderer)
        val stateMachine = SignInStateMachine(view.actions, RealSignInService(), Schedulers.computation())

        disposable += view.render(stateMachine.state)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
