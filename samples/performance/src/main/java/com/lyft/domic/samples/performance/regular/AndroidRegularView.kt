package com.lyft.domic.samples.performance.regular

import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import com.lyft.domic.samples.performance.R

class AndroidRegularView(root: ViewGroup) : RegularView {

    private val counter0 = root.findViewById<TextView>(R.id.counter0)
    private val counter1 = root.findViewById<TextView>(R.id.counter1)
    private val counter2 = root.findViewById<TextView>(R.id.counter2)
    private val counter3 = root.findViewById<TextView>(R.id.counter3)
    private val counter4 = root.findViewById<TextView>(R.id.counter4)
    private val counter5 = root.findViewById<TextView>(R.id.counter5)
    private val counter6 = root.findViewById<TextView>(R.id.counter6)
    private val counter7 = root.findViewById<TextView>(R.id.counter7)
    private val counter8 = root.findViewById<TextView>(R.id.counter8)
    private val counter9 = root.findViewById<TextView>(R.id.counter9)

    private val button = root.findViewById<Button>(R.id.button)
    private val checkBox = root.findViewById<CheckBox>(R.id.checkbox)
    private val radioButton = root.findViewById<RadioButton>(R.id.radiobutton)

    override fun setCounter0Text(text: CharSequence) = counter0.setText(text)
    override fun setCounter1Text(text: CharSequence) = counter1.setText(text)
    override fun setCounter2Text(text: CharSequence) = counter2.setText(text)
    override fun setCounter3Text(text: CharSequence) = counter3.setText(text)
    override fun setCounter4Text(text: CharSequence) = counter4.setText(text)
    override fun setCounter5Text(text: CharSequence) = counter5.setText(text)
    override fun setCounter6Text(text: CharSequence) = counter6.setText(text)
    override fun setCounter7Text(text: CharSequence) = counter7.setText(text)
    override fun setCounter8Text(text: CharSequence) = counter8.setText(text)
    override fun setCounter9Text(text: CharSequence) = counter9.setText(text)

    override fun setButtonEnabled(enabled: Boolean) = button.setEnabled(enabled)
    override fun setCheckBoxChecked(checked: Boolean) = checkBox.setChecked(checked)
    override fun setRadioButtonChecked(checked: Boolean) = radioButton.setChecked(checked)
}
