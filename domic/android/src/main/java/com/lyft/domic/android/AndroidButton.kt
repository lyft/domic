package com.lyft.domic.android

import com.lyft.domic.api.Button
import com.lyft.domic.api.TextView
import com.lyft.domic.api.rendering.Renderer

class AndroidButton(
        private val realButton: android.widget.Button,
        private val renderer: Renderer
) : Button {

    private val asTextView: TextView = AndroidTextView(realButton, renderer)

    override val observe: Button.Observe = object : Button.Observe, TextView.Observe by asTextView.observe {

    }

    override val change: Button.Change = object : Button.Change, TextView.Change by asTextView.change {

    }
}
