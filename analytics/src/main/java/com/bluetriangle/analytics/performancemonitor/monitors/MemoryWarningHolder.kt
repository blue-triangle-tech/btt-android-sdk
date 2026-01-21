package com.bluetriangle.analytics.performancemonitor.monitors

import android.util.Log
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.eventhub.SDKEventConsumer
import com.bluetriangle.analytics.eventhub.SDKEventHub

class MemoryWarningHolder: SDKEventConsumer {

    init {
        SDKEventHub.instance.addConsumer(this)
    }

    private var memoryWarnings = mutableMapOf<Long, MemoryMonitor.MemoryWarningException>()

    internal fun recordMemoryWarning(timer: Timer, memoryWarning: MemoryMonitor.MemoryWarningException) {
        synchronized(memoryWarnings) {
            if(!memoryWarnings.containsKey(timer.start)) {
                memoryWarnings[timer.start] = memoryWarning
            } else {
                memoryWarnings[timer.start]?.let {
                    it.count++
                }
            }
        }
    }

    private fun submitMemoryWarnings(timer: Timer) {
        synchronized(memoryWarnings) {
            memoryWarnings[timer.start]?.let {
                Tracker.instance?.memoryWarningReporter?.reportMemoryWarning(timer, it)
            }
            memoryWarnings.remove(timer.start)
        }
    }

    override fun onTimerSubmitted(timer: Timer) {
        super.onTimerSubmitted(timer)
        Log.d("BlueTriangle", "MemoryWarningHolder::onTimerSubmitted")
        submitMemoryWarnings(timer)
    }

    fun stop() {
        SDKEventHub.instance.removeConsumer(this)
    }

}