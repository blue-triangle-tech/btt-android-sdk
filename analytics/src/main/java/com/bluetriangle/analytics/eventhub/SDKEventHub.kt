package com.bluetriangle.analytics.eventhub

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.eventhub.helpers.ActivityEventHandler
import java.lang.ref.WeakReference

internal class SDKEventHub private constructor(): SDKEventConsumer {

    companion object {
        private var _instance: SDKEventHub? = null

        val instance: SDKEventHub
            get() {
                if (_instance == null) {
                    _instance = SDKEventHub()
                }
                return _instance!!
            }
    }


    private val consumers = arrayListOf<WeakReference<SDKEventConsumer>>()

    fun addConsumer(consumer: SDKEventConsumer) {
        synchronized(consumers) {
            if(consumers.find { it.get() == consumer } == null) {
                consumers.add(WeakReference(consumer))
            }
        }
    }

    fun removeConsumer(consumer: SDKEventConsumer) {
        synchronized(consumers) {
            consumers.removeAll { reference -> reference.get() == consumer }
        }
    }

    private fun notifyConsumers(notify:(SDKEventConsumer)-> Unit) {
        synchronized(consumers) {
            consumers.forEach { consumer ->
                consumer.get()?.let {
                    notify(it)
                }
            }
        }
    }

    override fun onTimerStarted(timer: Timer) {
        notifyConsumers {
            it.onTimerStarted(timer)
        }
    }

    override fun onTimerSubmitted(timer: Timer) {
        notifyConsumers {
            it.onTimerSubmitted(timer)
        }
    }
}