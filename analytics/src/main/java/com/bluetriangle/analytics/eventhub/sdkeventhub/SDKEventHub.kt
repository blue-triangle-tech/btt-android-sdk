package com.bluetriangle.analytics.eventhub.sdkeventhub

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.launchtime.model.LaunchType
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkstate.BTTNetworkState
import java.lang.ref.WeakReference

internal class SDKEventHub private constructor() {

    companion object {
        private var _instance: SDKEventHub? = null
        val instance: SDKEventHub
            get() {
                if (_instance == null) _instance = SDKEventHub()
                return _instance!!
            }
    }

    private val consumers = HashMap<Class<out SDKEventConsumer>, ArrayList<WeakReference<SDKEventConsumer>>>()

    fun addConsumer(consumer: SDKEventConsumer) {
        if (consumer is TimerEventConsumer) { register(TimerEventConsumer::class.java, consumer) }
        if (consumer is NetworkEventConsumer) { register(NetworkEventConsumer::class.java, consumer) }
        if (consumer is AppLifecycleEventConsumer) { register(AppLifecycleEventConsumer::class.java, consumer) }
    }

    fun removeConsumer(consumer: SDKEventConsumer) {
        if (consumer is TimerEventConsumer) unregister(TimerEventConsumer::class.java, consumer)
        if (consumer is NetworkEventConsumer) unregister(NetworkEventConsumer::class.java, consumer)
        if (consumer is AppLifecycleEventConsumer) unregister(AppLifecycleEventConsumer::class.java, consumer)
    }

    private fun register(type: Class<out SDKEventConsumer>, consumer: SDKEventConsumer) {
        synchronized(consumers) {
            val list = consumers.getOrPut(type) { ArrayList() }
            if (list.none { it.get() == consumer }) list.add(WeakReference(consumer))
        }
    }

    private fun unregister(type: Class<out SDKEventConsumer>, consumer: SDKEventConsumer) {
        synchronized(consumers) {
            consumers[type]?.removeAll { it.get() == consumer }
        }
    }

    private fun <T : SDKEventConsumer> notify(type: Class<T>, action: (T) -> Unit) {
        synchronized(consumers) {
            consumers[type]?.forEach { it.get()?.let { consumer -> action(consumer as T) } }
        }
    }

    fun onTimerStarted(timer: Timer) = notify(TimerEventConsumer::class.java) {
        it.onTimerStarted(timer)
    }
    fun onTimerSubmitted(timer: Timer) = notify(TimerEventConsumer::class.java) {
        it.onTimerSubmitted(timer)
    }
    fun onNetworkRequestCaptured(request: CapturedRequest) = notify(NetworkEventConsumer::class.java) {
        it.onNetworkRequestCaptured(request)
    }
    fun onNetworkStateChanged(state: BTTNetworkState) = notify(NetworkEventConsumer::class.java) {
        it.onNetworkStateChanged(state)
    }

    fun onLaunchDetected(launchType: LaunchType) = notify(AppLifecycleEventConsumer::class.java) {
        it.onLaunchDetected(launchType)
    }
    fun onAppInstall(appVersion: String) = notify(AppLifecycleEventConsumer::class.java) {
        it.onAppInstall(appVersion)
    }
    fun onAppUpdate(oldVersion: String, newVersion: String) = notify(AppLifecycleEventConsumer::class.java) {
        it.onAppUpdate(oldVersion, newVersion)
    }

}