package com.lyft.domic.samples.performance.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lyft.domic.samples.performance.PerformanceApplication
import com.lyft.domic.samples.performance.R
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = AndroidMainView(findViewById(android.R.id.content), PerformanceApplication.renderer)

        disposable += Single
                .fromCallable { MainViewModel(view) }
                .subscribeOn(Schedulers.computation())
                .subscribe { viewModel -> disposable += viewModel }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
