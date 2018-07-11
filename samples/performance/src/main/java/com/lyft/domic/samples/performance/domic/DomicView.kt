package com.lyft.domic.samples.performance.domic

import com.lyft.domic.api.Button
import com.lyft.domic.api.CompoundButton
import com.lyft.domic.api.EditText
import com.lyft.domic.api.TextView

interface DomicView {
    val counter0: TextView
    val counter1: TextView
    val counter2: TextView
    val counter3: TextView
    val counter4: TextView
    val counter5: TextView
    val counter6: TextView
    val counter7: TextView
    val counter8: TextView
    val counter9: TextView

    val button: Button
    val checkBox: CompoundButton
    val radioButton: CompoundButton
}
