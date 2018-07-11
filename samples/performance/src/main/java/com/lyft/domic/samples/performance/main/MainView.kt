package com.lyft.domic.samples.performance.main

import android.support.annotation.AnyThread
import com.lyft.domic.api.Button
import io.reactivex.Completable

interface MainView {
    val regularUiButton: Button
    val domicButton: Button

    @AnyThread
    fun navigateToRegular(): Completable

    @AnyThread
    fun navigateToDomic(): Completable
}
