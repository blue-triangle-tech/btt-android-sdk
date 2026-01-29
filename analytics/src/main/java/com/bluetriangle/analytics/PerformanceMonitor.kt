package com.bluetriangle.analytics

import android.os.Process
import com.bluetriangle.analytics.performancemonitor.PerformanceListener
import com.bluetriangle.analytics.performancemonitor.monitors.CpuMonitor
import com.bluetriangle.analytics.performancemonitor.monitors.MainThreadMonitor
import com.bluetriangle.analytics.performancemonitor.monitors.MemoryMonitor
import java.lang.ref.WeakReference

class PerformanceMonitor(configuration: BlueTriangleConfiguration) : Thread(THREAD_NAME) {
    private val logger = configuration.logger
    private var isRunning = true
    private val interval = configuration.performanceMonitorIntervalMs

    private val metricMonitors = listOf(
        CpuMonitor(configuration),
        MemoryMonitor(configuration),
        MainThreadMonitor(configuration)
    )

    private val listeners = mutableListOf<WeakReference<PerformanceListener>>()

    fun registerListener(listener: PerformanceListener) {
        synchronized(listeners) {
            listeners.add(WeakReference(listener))
        }
    }

    fun unregisterListener(listener: PerformanceListener) {
        synchronized(listeners) {
            listeners.removeAll { it.get() == listener }
        }
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

        synchronized(listeners) {
            listeners.forEach {
                it.get()?.onDataReceived(dataPoints)
            }
        }
    }

    @Synchronized
    fun stopRunning() {
        isRunning = false
        metricMonitors.forEach { it.end() }
    }

    companion object {
        private const val THREAD_NAME = "BTTPerformanceMonitor"
    }
}