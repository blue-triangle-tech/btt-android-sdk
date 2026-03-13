package com.bluetriangle.analytics.lifecycle

internal interface KeyboardEventObserver {
    fun onKeyboardShown()
    fun onKeyboardHidden()
}