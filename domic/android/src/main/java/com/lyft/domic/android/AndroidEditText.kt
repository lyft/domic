package com.lyft.domic.android

import com.lyft.domic.api.EditText
import com.lyft.domic.api.TextView
import com.lyft.domic.util.distinctUntilChanged
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicReferenceArray

class AndroidEditText(private val realEditText: android.widget.EditText) : EditText {

    private val asTextView: TextView = AndroidTextView(realEditText)

    override val observe: EditText.Observe = object : EditText.Observe, TextView.Observe by asTextView.observe {

    }

    override val change: EditText.Change = object : EditText.Change, TextView.Change by asTextView.change {

        private val state = AtomicReferenceArray<Any>(1)

        override fun selection(selectionValues: Observable<Int>): Disposable {
            return selectionValues
                    .distinctUntilChanged(state, 0)
                    .observeOn(mainThread())
                    .subscribe { realEditText.setSelection(it) }
        }
    }
}
