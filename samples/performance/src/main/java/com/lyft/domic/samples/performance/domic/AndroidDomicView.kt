package com.lyft.domic.samples.performance.domic

import android.view.ViewGroup
import com.lyft.domic.android.AndroidButton
import com.lyft.domic.android.AndroidCompoundButton
import com.lyft.domic.android.AndroidTextView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.samples.performance.R

class AndroidDomicView(root: ViewGroup, renderer: Renderer) : DomicView {

    override val counter0 = AndroidTextView(root.findViewById(R.id.counter0), renderer)
    override val counter1 = AndroidTextView(root.findViewById(R.id.counter1), renderer)
    override val counter2 = AndroidTextView(root.findViewById(R.id.counter2), renderer)
    override val counter3 = AndroidTextView(root.findViewById(R.id.counter3), renderer)
    override val counter4 = AndroidTextView(root.findViewById(R.id.counter4), renderer)
    override val counter5 = AndroidTextView(root.findViewById(R.id.counter5), renderer)
    override val counter6 = AndroidTextView(root.findViewById(R.id.counter6), renderer)
    override val counter7 = AndroidTextView(root.findViewById(R.id.counter7), renderer)
    override val counter8 = AndroidTextView(root.findViewById(R.id.counter8), renderer)
    override val counter9 = AndroidTextView(root.findViewById(R.id.counter9), renderer)

    override val button = AndroidButton(root.findViewById(R.id.button), renderer)
    override val checkBox = AndroidCompoundButton(root.findViewById(R.id.checkbox), renderer)
    override val radioButton = AndroidCompoundButton(root.findViewById(R.id.radiobutton), renderer)
}
