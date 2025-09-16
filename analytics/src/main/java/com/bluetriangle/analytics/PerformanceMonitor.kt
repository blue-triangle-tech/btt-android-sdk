package com.bluetriangle.analytics

import android.os.Process
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider
import com.bluetriangle.analytics.performancemonitor.CpuMonitor
import com.bluetriangle.analytics.performancemonitor.MainThreadMonitor
import com.bluetriangle.analytics.performancemonitor.MemoryMonitor
import com.bluetriangle.analytics.performancemonitor.MetricMonitor
import com.bluetriangle.analytics.performancemonitor.PerformanceMetric

class PerformanceMonitor(configuration: BlueTriangleConfiguration, deviceInfoProvider: IDeviceInfoProvider) : Thread(THREAD_NAME) {
    private val logger = configuration.logger
    private var isRunning = true
    private val interval = configuration.performanceMonitorIntervalMs

    private val metricMonitors = arrayListOf<MetricMonitor>()

    init {
        metricMonitors.add(CpuMonitor(configuration))
        metricMonitors.add(MemoryMonitor(configuration, deviceInfoProvider))
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

    val performanceReport: Map<PerformanceMetric, String>
        get() = buildMap {
            metricMonitors.forEach { putAll(it.metricFields) }
        }

    val analyticsPerformanceReport: Map<String, String>
        get() = performanceReport.mapKeys { it.key.field }

    val wcdPerformanceReport: Map<String, String>
        get() = performanceReport.filter { it.key != PerformanceMetric.TotalMemory }.mapKeys { it.key.field }

    companion object {
        private const val THREAD_NAME = "BTTPerformanceMonitor"
    }
}