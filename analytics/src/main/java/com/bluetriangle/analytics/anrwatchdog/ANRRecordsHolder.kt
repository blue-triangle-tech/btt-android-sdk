package com.bluetriangle.analytics.anrwatchdog

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventHub
import com.bluetriangle.analytics.eventhub.sdkeventhub.TimerEventConsumer

internal class ANRRecordsHolder: TimerEventConsumer {

    init {
        SDKEventHub.instance.addConsumer(this)
    }

    private val anrRecords = mutableMapOf<Long, MutableList<ANRWarningException>>()

    /**
     * Records the ANR warning
     */
    fun recordANR(timer: Timer, warning: ANRWarningException) {
        synchronized(anrRecords) {
            val key = timer.start
            if(!anrRecords.containsKey(key)) {
                anrRecords[key] = mutableListOf()
            }
            anrRecords[key]?.add(warning)
        }
    }

    private fun submitANRs(timer: Timer) {
        val anrRecord = synchronized(anrRecords) {
            anrRecords.remove(timer.start)
        }
        anrRecord?.let {
            for(record in it) {
                Tracker.instance?.anrReporter?.reportANR(timer, record)
            }
        }
    }

    /**
     * called by SDKEventHub when any Timer is submitted
     */
    override fun onTimerSubmitted(timer: Timer) {
        submitANRs(timer)
    }

    fun stop() {
        SDKEventHub.instance.removeConsumer(this)
    }
}