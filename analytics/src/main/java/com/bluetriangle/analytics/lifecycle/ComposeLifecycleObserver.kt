package com.bluetriangle.analytics.lifecycle

interface ComposeLifecycleObserver {

    fun onEnterComposition(name: String) {

    }

    fun onLeaveComposition(name: String) {

    }
}