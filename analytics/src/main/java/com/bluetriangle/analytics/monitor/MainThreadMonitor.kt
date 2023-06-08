package com.bluetriangle.analytics.monitor

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.PerformanceReport.Companion.FIELD_MAX_MAIN_THREAD_BLOCK
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.anrwatchdog.AnrException

class MainThreadMonitor(configuration: BlueTriangleConfiguration) : MetricMonitor {
    private val logger = configuration.logger

    override val metricFields: Map<String, String>
        get() = mapOf(
            FIELD_MAX_MAIN_THREAD_BLOCK to maxMainThreadBlock.toString()
        )

    private val isTrackAnrEnabled = configuration.isTrackAnrEnabled
    private val trackAnrIntervalSec = configuration.trackAnrIntervalSec

    private val handler = Handler(Looper.getMainLooper())
    private var dummyTask = Runnable { }
    private var postTime: Long = 0L

    private var maxMainThreadBlock: Long = 0L

    private var isANRNotified = false

    override fun onBeforeSleep() {
        if (!handler.hasMessages(0)) {
            postTime = System.currentTimeMillis()
            handler.postAtFrontOfQueue(dummyTask)
        }
    }

    override fun onAfterSleep() {
        val threadBlockDelay = System.currentTimeMillis() - postTime

        maxMainThreadBlock = maxMainThreadBlock.coerceAtLeast(threadBlockDelay)

        if (isTrackAnrEnabled && threadBlockDelay > (trackAnrIntervalSec * 1000L)) {
            if (!isANRNotified) {
                isANRNotified = true
                logger?.debug("Sending ANR as Exception")
                Tracker.instance?.trackException(
                    "ANR Detected",
                    AnrException(threadBlockDelay),
                    Tracker.BTErrorType.ANRWarning
                )
            }
        } else {
            isANRNotified = false
        }
    }
}