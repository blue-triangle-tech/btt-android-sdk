package com.bluetriangle.analytics.monitor

import android.app.ActivityManager
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.PerformanceReport
import com.bluetriangle.analytics.Tracker

internal class MemoryMonitor(configuration: BlueTriangleConfiguration): MetricMonitor {
    override val metricFields: Map<String, String>
        get() = mapOf(
            PerformanceReport.FIELD_MIN_MEMORY to minMemory.toString(),
            PerformanceReport.FIELD_MAX_MEMORY to maxMemory.toString(),
            PerformanceReport.FIELD_AVG_MEMORY to calculateAverageMemory().toString()
        )

    private val activityManager = Tracker.instance?.activityManager
    private var minMemory = Long.MAX_VALUE
    private var maxMemory: Long = 0
    private var cumulativeMemory: Long = 0
    private var memoryCount: Long = 0
    private val logger = configuration.logger

    private fun calculateAverageMemory(): Long {
        return if (memoryCount == 0L) {
            0
        } else cumulativeMemory / memoryCount
    }

    override fun onBeforeSleep() {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        val usedMemory = memoryInfo.totalMem - memoryInfo.availMem
        logger?.debug("Used Memory: $usedMemory", )
        updateMemory(usedMemory)
    }

    private fun updateMemory(memory: Long) {
        if (memory < minMemory) {
            minMemory = memory
        }
        if (memory > maxMemory) {
            maxMemory = memory
        }
        cumulativeMemory += memory
        memoryCount++
    }

    override fun onAfterSleep() {

    }
}