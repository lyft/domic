package com.lyft.domic.android

import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.lyft.domic.android.annotations.MutatedByFramework
import com.lyft.domic.api.Button
import com.lyft.domic.api.CompoundButton
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.util.distinctUntilChanged
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidCompoundButton(
        private val realCompoundButton: android.widget.CompoundButton,
        private val renderer: Renderer
) : CompoundButton {

    companion object {
        private const val STATE_INDEX_CHECKED = 0
    }

    private val asButton = AndroidButton(realCompoundButton, renderer)

    override val observe: CompoundButton.Observe = object : CompoundButton.Observe, Button.Observe by asButton.observe {

        @MutatedByFramework
        override val checked: Observable<out Boolean> =
                RxCompoundButton
                        .checkedChanges(realCompoundButton)
                        .subscribeOn(mainThread())
                        .share()

    }

    override val change: CompoundButton.Change = object : CompoundButton.Change, Button.Change by asButton.change {

        private val state = AtomicReferenceArray<Any>(1)

        init {
            observe
                    .checked
                    .subscribe { state.set(STATE_INDEX_CHECKED, it) }
        }

        override fun checked(checkedValues: Observable<Boolean>): Disposable = checkedValues
                .distinctUntilChanged(state, STATE_INDEX_CHECKED)
                .map { Action { realCompoundButton.isChecked = it } }
                .subscribe(renderer::render)
    }
}