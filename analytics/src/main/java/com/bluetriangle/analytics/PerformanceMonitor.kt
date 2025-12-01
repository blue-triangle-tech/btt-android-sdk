package com.bluetriangle.analytics

import android.os.Process
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider
import com.bluetriangle.analytics.performancemonitor.monitors.CpuMonitor
import com.bluetriangle.analytics.performancemonitor.monitors.MainThreadMonitor
import com.bluetriangle.analytics.performancemonitor.monitors.MemoryMonitor
import com.bluetriangle.analytics.performancemonitor.PerformanceListener
import java.lang.ref.WeakReference
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.removeAll

class PerformanceMonitor(configuration: BlueTriangleConfiguration, deviceInfoProvider: IDeviceInfoProvider) : Thread(THREAD_NAME) {
    private val logger = configuration.logger
    private var isRunning = true
    private val interval = configuration.performanceMonitorIntervalMs

    private val metricMonitors = listOf(
        CpuMonitor(configuration),
        MemoryMonitor(configuration, deviceInfoProvider),
        MainThreadMonitor(configuration)
    )

    private val listeners = mutableListOf<WeakReference<PerformanceListener>>()

    fun registerListener(listener: PerformanceListener) {
        listeners.add(WeakReference(listener))
    }

    fun unregisterListener(listener: PerformanceListener) {
        listeners.removeAll { it.get() == listener }
    }

    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST)
        while (isRunning) {
            try {
                setupMetrics()

                if (isRunning) {
                    sleep(interval)
                }

                captureDataPoints()
            } catch (e: InterruptedException) {
                logger?.error(e, "Performance Monitor thread interrupted")
            }
        }
    }

    private fun setupMetrics() {
        metricMonitors.forEach { it.setupMetric() }
    }

    private fun captureDataPoints() {
        val dataPoints = metricMonitors.map { it.captureDataPoint() }

        listeners.forEach { it.get()?.onDataReceived(dataPoints) }
    }

    fun stopRunning() {
        isRunning = false
    }

    companion object {
        private const val THREAD_NAME = "BTTPerformanceMonitor"
    }
}