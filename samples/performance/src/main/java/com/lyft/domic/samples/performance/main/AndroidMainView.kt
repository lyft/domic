package com.lyft.domic.samples.performance.main

import android.content.Intent
import android.view.ViewGroup
import com.lyft.domic.android.AndroidButton
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.samples.performance.R
import com.lyft.domic.samples.performance.domic.DomicActivity
import com.lyft.domic.samples.performance.regular.RegularActivity
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread

class AndroidMainView(private val root: ViewGroup, renderer: Renderer) : MainView {
    override val regularUiButton = AndroidButton(root.findViewById(R.id.button_regular_ui), renderer)
    override val domicButton = AndroidButton(root.findViewById(R.id.button_domic), renderer)

    override fun navigateToRegular() = Completable
            .fromAction { root.context.startActivity(Intent(root.context, RegularActivity::class.java)) }
            .subscribeOn(mainThread())

    override fun navigateToDomic() = Completable
            .fromAction { root.context.startActivity(Intent(root.context, DomicActivity::class.java)) }
            .subscribeOn(mainThread())
}
