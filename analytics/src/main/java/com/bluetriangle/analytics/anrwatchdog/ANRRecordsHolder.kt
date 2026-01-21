package com.bluetriangle.analytics.anrwatchdog

import android.util.Log
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.eventhub.SDKEventConsumer
import com.bluetriangle.analytics.eventhub.SDKEventHub

internal class ANRRecordsHolder: SDKEventConsumer {

    init {
        SDKEventHub.instance.addConsumer(this)
    }

    private val anrRecords = mutableMapOf<Long, MutableList<AnrException>>()

    fun recordANR(timer: Timer, error: AnrException) {
        synchronized(anrRecords) {
            if(!anrRecords.containsKey(timer.start)) {
                anrRecords[timer.start] = mutableListOf()
            }
            anrRecords[timer.start]?.add(error)
        }
    }

    private fun submitANRs(timer: Timer) {
        synchronized(anrRecords) {
            if(anrRecords.containsKey(timer.start)) {
                anrRecords[timer.start]?.let {
                    for(record in it) {
                        Tracker.instance?.anrReporter?.reportANR(timer, record)
                    }
                }
                anrRecords.remove(timer.start)
            }
        }
    }

    override fun onTimerSubmitted(timer: Timer) {
        super.onTimerSubmitted(timer)
        Log.d("BlueTriangle", "ANRRecordsHolder::onTimerSubmitted")
        submitANRs(timer)
    }

    fun stop() {
        SDKEventHub.instance.removeConsumer(this)
    }
}