package com.bluetriangle.analytics.anrwatchdog

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker

internal class ANRRecordsHolder {

    private val anrRecords = mutableMapOf<Long, MutableList<AnrException>>()

    fun recordANR(timer: Timer, error: AnrException) {
        synchronized(anrRecords) {
            if(!anrRecords.containsKey(timer.start)) {
                anrRecords[timer.start] = mutableListOf()
            }
            anrRecords[timer.start]?.add(error)
        }
    }

    fun submitANRs(timer: Timer) {
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

}