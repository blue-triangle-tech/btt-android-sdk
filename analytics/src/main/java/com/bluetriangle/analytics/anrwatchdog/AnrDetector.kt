package com.bluetriangle.analytics.anrwatchdog

import java.lang.ref.WeakReference

internal abstract class AnrDetector {

    protected var listeners = hashMapOf<String, WeakReference<AnrListener>>()

    fun addAnrListener(tag:String, listener: AnrListener) {
        listeners[tag] = WeakReference(listener)
    }

    fun removeAnrListener(tag: String) {
        listeners.remove(tag)
    }

    protected fun notifyListeners(error: AnrException) {
        listeners.forEach { listener ->
            listener.value.get()?.onAppNotResponding(error)
        }
    }

    abstract fun startDetection()

    abstract fun stopDetection()

}