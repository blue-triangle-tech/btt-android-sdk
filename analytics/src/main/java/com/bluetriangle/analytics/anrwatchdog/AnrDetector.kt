package com.bluetriangle.analytics.anrwatchdog

import java.lang.ref.WeakReference

abstract class AnrDetector {

    protected val listeners = hashMapOf<String, WeakReference<AnrListener>>()

    fun addAnrListener(tag: String, listener: AnrListener) {
        listeners[tag] = WeakReference(listener)
    }

    fun removeAnrListener(tag: String) {
        listeners.remove(tag)
    }

    protected fun notifyListeners(error: AnrException) {
        for (listener in listeners) {
            listener.value.get()?.onAppNotResponding(error)
        }
    }

    abstract fun startDetection()

    abstract fun stopDetection()

}