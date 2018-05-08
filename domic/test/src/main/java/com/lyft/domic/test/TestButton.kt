package com.lyft.domic.test

import com.lyft.domic.api.Button
import com.lyft.domic.api.TextView

class TestButton : Button {

    interface Check : TestTextView.Check

    interface Simulate : TestTextView.Simulate

    private val asTestTextView = TestTextView()

    override val observe: Button.Observe = object : Button.Observe, TextView.Observe by asTestTextView.observe {

    }

    override val change: Button.Change = object : Button.Change, TextView.Change by asTestTextView.change {

    }

    val check: Check = object : Check, TestTextView.Check by asTestTextView.check {

    }

    val simulate: Simulate = object : Simulate, TestTextView.Simulate by asTestTextView.simulate {

    }
}
