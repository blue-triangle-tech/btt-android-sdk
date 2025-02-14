package com.bluetriangle.analytics.sdkevents

import android.app.Application
import android.util.Log
import java.lang.ref.WeakReference

object SDKEventsManager {

    private val sdkEventsListeners = arrayListOf<WeakReference<SDKEventsListener>>()

    fun registerConfigurationEventListener(listener: SDKEventsListener) {
        val index = sdkEventsListeners.indexOfFirst { it.get() == listener }
        if(index >= 0) return

        sdkEventsListeners.add(WeakReference(listener))
    }

    fun unregisterConfigurationEventListener(listener: SDKEventsListener) {
        sdkEventsListeners.removeAll { it.get() == listener }
    }

    private var application: Application ?= null

    fun notifyConfigured(application: Application, sdkConfiguration: SDKConfiguration) {
        SDKEventsManager.application = application

        Log.d("BlueTriangle", "ClarityInitializer: notifyConfigured: ${sdkEventsListeners.map { it.get() }}")
        sdkEventsListeners.forEach {
            it.get()?.onConfigured(application, sdkConfiguration)
        }
    }

    fun notifyEnabled(sdkConfiguration: SDKConfiguration) {
        val app = application ?:return

        Log.d("BlueTriangle", "ClarityInitializer: notifyEnabled: ${sdkEventsListeners.map { it.get() }}")
        sdkEventsListeners.forEach {
            it.get()?.onEnabled(app, sdkConfiguration)
        }
    }

    fun notifyDisabled(sdkConfiguration: SDKConfiguration) {
        val app = application ?:return

        Log.d("BlueTriangle", "ClarityInitializer: notifyDisabled: ${sdkEventsListeners.map { it.get() }}")
        sdkEventsListeners.forEach {
            it.get()?.onDisabled(app, sdkConfiguration)
        }
    }

    fun notifySessionChanged(sdkConfiguration: SDKConfiguration) {
        val app = application ?:return

        Log.d("BlueTriangle", "ClarityInitializer: notifySessionChanged: ${sdkEventsListeners.map { it.get() }}")
        sdkEventsListeners.forEach {
            it.get()?.onSessionChanged(app, sdkConfiguration)
        }
    }
}