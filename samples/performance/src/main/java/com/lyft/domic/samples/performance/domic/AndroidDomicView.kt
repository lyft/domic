package com.lyft.domic.samples.performance.domic

import android.view.ViewGroup
import com.lyft.domic.android.AndroidButton
import com.lyft.domic.android.AndroidCompoundButton
import com.lyft.domic.android.AndroidEditText
import com.lyft.domic.android.AndroidTextView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.samples.performance.R

class AndroidDomicView(root: ViewGroup, renderer: Renderer) : DomicView {

    override val counter0 by lazy { AndroidTextView(root.findViewById(R.id.counter0), renderer) }
    override val counter1 by lazy { AndroidTextView(root.findViewById(R.id.counter1), renderer) }
    override val counter2 by lazy { AndroidTextView(root.findViewById(R.id.counter2), renderer) }
    override val counter3 by lazy { AndroidTextView(root.findViewById(R.id.counter3), renderer) }
    override val counter4 by lazy { AndroidTextView(root.findViewById(R.id.counter4), renderer) }
    override val counter5 by lazy { AndroidTextView(root.findViewById(R.id.counter5), renderer) }
    override val counter6 by lazy { AndroidTextView(root.findViewById(R.id.counter6), renderer) }
    override val counter7 by lazy { AndroidTextView(root.findViewById(R.id.counter7), renderer) }
    override val counter8 by lazy { AndroidTextView(root.findViewById(R.id.counter8), renderer) }
    override val counter9 by lazy { AndroidTextView(root.findViewById(R.id.counter9), renderer) }

    override val button by lazy { AndroidButton(root.findViewById(R.id.button), renderer) }
    override val checkBox by lazy { AndroidCompoundButton(root.findViewById(R.id.checkbox), renderer) }
    override val radioButton by lazy { AndroidCompoundButton(root.findViewById(R.id.radiobutton), renderer) }

    override val editText1 by lazy { AndroidEditText(root.findViewById(R.id.edittext1), renderer) }
    override val editText2 by lazy { AndroidEditText(root.findViewById(R.id.edittext2), renderer) }
}
