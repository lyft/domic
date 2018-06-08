package com.lyft.domic.android

import com.lyft.domic.api.EditText
import com.lyft.domic.api.TextView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.util.distinctUntilChanged
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidEditText(
        private val realEditText: android.widget.EditText,
        private val renderer: Renderer
) : EditText {

    private val asTextView: TextView = AndroidTextView(realEditText, renderer)
    private val state = AtomicReferenceArray<Any>(1)

    override val observe: EditText.Observe = object : EditText.Observe, TextView.Observe by asTextView.observe {

    }

    override val change: EditText.Change = object : EditText.Change, TextView.Change by asTextView.change {

        override fun selection(selectionValues: Observable<Int>): Disposable {
            return selectionValues
                    .distinctUntilChanged(state, 0)
                    .map { Action { realEditText.setSelection(it) } }
                    .subscribe(renderer::render)
        }
    }
}
