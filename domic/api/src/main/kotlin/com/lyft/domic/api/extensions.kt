package com.lyft.domic.api

import com.lyft.domic.api.rendering.Change
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

inline fun <T> Observable<T>.subscribe(crossinline bindFunc: (stream: Observable<T>) -> Disposable): Disposable =
        bindFunc.invoke(this)

