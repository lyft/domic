package com.lyft.domic.samples.performance.regular

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lyft.domic.samples.performance.PerformanceApplication
import com.lyft.domic.samples.performance.R
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class RegularActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance)

        val view = AndroidRegularView(findViewById(android.R.id.content))

        disposable += Single
                .fromCallable { RegularPresenter(view) }
                .subscribeOn(Schedulers.computation())
                .subscribe { presenter -> disposable += presenter }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
