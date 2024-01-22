package com.bluetriangle.analytics.okhttp

import com.bluetriangle.analytics.BlueTriangleConfiguration
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.internal.asFactory

class BlueTriangleOkHttpEventListenerFactory @JvmOverloads constructor(
    private val configuration: BlueTriangleConfiguration,
    private val eventListenerFactory: EventListener.Factory? = null
) : EventListener.Factory {
    constructor(configuration: BlueTriangleConfiguration, eventListener: EventListener) : this(
        configuration,
        eventListener.asFactory()
    )

    override fun create(call: Call): EventListener {
        return BlueTriangleOkHttpEventListener(configuration, eventListenerFactory?.create(call))
    }
}