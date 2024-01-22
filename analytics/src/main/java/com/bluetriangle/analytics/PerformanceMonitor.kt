package com.bluetriangle.analytics

import android.os.Process
import com.bluetriangle.analytics.monitor.CpuMonitor
import com.bluetriangle.analytics.monitor.MainThreadMonitor
import com.bluetriangle.analytics.monitor.MemoryMonitor
import com.bluetriangle.analytics.monitor.MetricMonitor

class PerformanceMonitor(configuration: BlueTriangleConfiguration) : Thread(THREAD_NAME) {
    private val logger = configuration.logger
    private var isRunning = true
    private val interval = configuration.performanceMonitorIntervalMs

    private val metricMonitors = arrayListOf<MetricMonitor>()

    init {
        metricMonitors.add(CpuMonitor(configuration))
        metricMonitors.add(MemoryMonitor(configuration))
        metricMonitors.add(MainThreadMonitor(configuration))
    }

    fun onTimerSubmit(timer: Timer) {
        metricMonitors.forEach {
            it.onTimerSubmit(timer)
        }
    }

    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST)
        while (isRunning) {
            try {
                metricOnBefore()

                if (isRunning) {
                    sleep(interval)
                }

                metricOnAfter()
            } catch (e: InterruptedException) {
                logger?.error(e, "Performance Monitor thread interrupted")
            }
        }
    }

    private fun metricOnBefore() {
        metricMonitors.forEach { it.onBeforeSleep() }
    }

    private fun metricOnAfter() {
        metricMonitors.forEach { it.onAfterSleep() }
    }

    fun stopRunning() {
        isRunning = false
    }

    val maxMainThreadUsage: Long
        get() {
            val mainThreadMonitor = metricMonitors.firstOrNull { it is MainThreadMonitor }
            return (mainThreadMonitor as? MainThreadMonitor)?.maxMainThreadBlock?:0L
        }

    val performanceReport: Map<String, String>
        get() {
            val metrics = hashMapOf<String, String>()
            metricMonitors.forEach { metrics.putAll(it.metricFields) }
            return metrics
        }

    companion object {
        private const val THREAD_NAME = "BTTPerformanceMonitor"
    }
}