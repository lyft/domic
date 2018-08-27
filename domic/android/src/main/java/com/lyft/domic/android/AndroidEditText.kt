package com.lyft.domic.android

import com.lyft.domic.android.rendering.mapToChange
import com.lyft.domic.api.EditText
import com.lyft.domic.api.TextView
import com.lyft.domic.api.rendering.Renderer
import com.lyft.domic.api.subscribe
import com.lyft.domic.util.sharedDistinctUntilChanged
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidEditText(
        private val realEditText: android.widget.EditText,
        private val renderer: Renderer
) : EditText {

    companion object {
        private const val STATE_INDEX_SELECTION = 0
    }

    private val asTextView: TextView = AndroidTextView(realEditText, renderer)
    private val state = AtomicReferenceArray<Any>(1)

    override val observe: EditText.Observe = object : EditText.Observe, TextView.Observe by asTextView.observe {

    }

    override val change: EditText.Change = object : EditText.Change, TextView.Change by asTextView.change {

        override fun selection(selectionValues: Observable<Int>): Disposable {
            return selectionValues
                    .sharedDistinctUntilChanged(state, STATE_INDEX_SELECTION)
                    .mapToChange(realEditText, STATE_INDEX_SELECTION) { realEditText.setSelection(it) }
                    .subscribe(renderer::render)
        }
    }
}
