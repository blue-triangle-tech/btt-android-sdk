package com.bluetriangle.analytics.lifecycle

import java.util.concurrent.CopyOnWriteArraySet

internal class ComposeLifecycleDispatcher {
    private val observers = CopyOnWriteArraySet<ComposeLifecycleObserver>()

    fun addObserver(observer: ComposeLifecycleObserver) {
        observers += observer
    }

    fun removeObserver(observer: ComposeLifecycleObserver) {
        observers -= observer
    }

    fun onEnterComposition(name: String) {
        observers.forEach { it.onEnterComposition(name) }
    }

    fun onLeaveComposition(name: String) {
        observers.forEach { it.onLeaveComposition(name) }
    }

    fun clear() {
        observers.clear()
    }
}