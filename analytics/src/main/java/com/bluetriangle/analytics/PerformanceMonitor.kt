package com.bluetriangle.analytics

import android.os.Process
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider
import com.bluetriangle.analytics.performancemonitor.monitors.CpuMonitor
import com.bluetriangle.analytics.performancemonitor.monitors.MainThreadMonitor
import com.bluetriangle.analytics.performancemonitor.monitors.MemoryMonitor
import com.bluetriangle.analytics.performancemonitor.PerformanceListener
import com.bluetriangle.analytics.performancemonitor.monitors.MemoryWarningReporter
import java.lang.ref.WeakReference
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.removeAll

class PerformanceMonitor(configuration: BlueTriangleConfiguration) : Thread(THREAD_NAME) {
    private val logger = configuration.logger
    private var isRunning = true
    private val interval = configuration.performanceMonitorIntervalMs

    internal val cpuMonitor = CpuMonitor(configuration)
    internal val memoryMonitor = MemoryMonitor(configuration)
    internal val mainThreadMonitor = MainThreadMonitor(configuration)

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
        cpuMonitor.setupMetric()
        memoryMonitor.setupMetric()
        mainThreadMonitor.setupMetric()
    }

    private fun captureDataPoints() {
        val dataPoints = buildList {
            add(cpuMonitor.captureDataPoint())
            add(memoryMonitor.captureDataPoint())
            add(mainThreadMonitor.captureDataPoint())
        }

        synchronized(listeners) {
            listeners.forEach {
                it.get()?.onDataReceived(dataPoints)
            }
        }
    }

    @Synchronized
    fun stopRunning() {
        isRunning = false
        cpuMonitor.end()
        memoryMonitor.end()
        mainThreadMonitor.end()
    }

    companion object {
        private const val THREAD_NAME = "BTTPerformanceMonitor"
    }
}