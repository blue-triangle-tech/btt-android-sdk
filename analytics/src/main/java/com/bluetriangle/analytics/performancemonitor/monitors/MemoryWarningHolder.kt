package com.bluetriangle.analytics.performancemonitor.monitors

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventHub
import com.bluetriangle.analytics.eventhub.sdkeventhub.TimerEventConsumer

class MemoryWarningHolder: TimerEventConsumer {

    init {
        SDKEventHub.instance.addConsumer(this)
    }

    private var memoryWarnings = mutableMapOf<Long, MemoryMonitor.MemoryWarningException>()

    internal fun recordMemoryWarning(timer: Timer, memoryWarning: MemoryMonitor.MemoryWarningException) {
        synchronized(memoryWarnings) {
            val key = timer.start
            if(!memoryWarnings.containsKey(key)) {
                memoryWarnings[key] = memoryWarning
            } else {
                memoryWarnings[key]?.let {
                    it.count++
                }
            }
        }
    }

    private fun submitMemoryWarnings(timer: Timer) {
        synchronized(memoryWarnings) {
            memoryWarnings.remove(timer.start)?.let {
                Tracker.instance?.memoryWarningReporter?.reportMemoryWarning(timer, it)
            }
        }
    }

    override fun onTimerSubmitted(timer: Timer) {
        submitMemoryWarnings(timer)
    }

    fun stop() {
        SDKEventHub.instance.removeConsumer(this)
    }

}