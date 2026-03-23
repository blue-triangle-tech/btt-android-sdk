package com.bluetriangle.analytics.lifecycle

import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.CopyOnWriteArraySet

internal class KeyboardEventDispatcher: OnApplyWindowInsetsListener {

    private val observers = CopyOnWriteArraySet<KeyboardEventObserver>()

    private var isShown = false

    fun addObserver(observer: KeyboardEventObserver) {
        observers += observer
    }

    fun removeObserver(observer: KeyboardEventObserver) {
        observers -= observer
    }

    override fun onApplyWindowInsets(
        view: View,
        insets: WindowInsetsCompat
    ): WindowInsetsCompat {
        val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

        if (imeVisible) {
            if(!isShown) {
                isShown = true
                observers.forEach { it.onKeyboardShown() }
            }
        } else {
            if(isShown) {
                isShown = false
                observers.forEach { it.onKeyboardHidden() }
            }
        }

        return insets
    }


    fun clear() {
        observers.clear()
    }

}