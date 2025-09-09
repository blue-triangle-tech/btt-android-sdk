package com.bluetriangle.analytics.performancemonitor

import android.util.Log
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Timer.Companion.FIELD_PAGE_NAME
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider

internal class MemoryMonitor(
    val configuration: BlueTriangleConfiguration,
    private val deviceInfoProvider: IDeviceInfoProvider
) : MetricMonitor {

    private val totalMemory = Runtime.getRuntime().maxMemory()

    override val metricFields: Map<PerformanceMetric, String>
        get() = mapOf(
            PerformanceMetric.MinMemory to minMemory.toString(),
            PerformanceMetric.MaxMemory to totalMemory.toString(),
            PerformanceMetric.AvgMemory to maxMemory.toString(),
            PerformanceMetric.TotalMemory to calculateAverageMemory().toString()
        )

    private var minMemory = Long.MAX_VALUE
    private var maxMemory: Long = 0
    private var cumulativeMemory: Long = 0
    private var memoryCount: Long = 0
    private val logger = configuration.logger
    private var memoryWarningException: MemoryWarningException? = null
    private val isVerboseDebug = configuration.isDebug || configuration.debugLevel == Log.VERBOSE
    private var memoryUsed = arrayListOf<Long>()

    private fun calculateAverageMemory(): Long {
        return if (memoryCount == 0L) {
            0
        } else cumulativeMemory / memoryCount
    }

    private var isMemoryThresholdReached = false

    private val Long.mb: Long
        get() = this / (1024 * 1024)

    class MemoryWarningException(val usedMemory: Long, val totalMemory: Long) :
        RuntimeException("Critical memory usage detected. App using ${usedMemory}MB of App's limit ${totalMemory}MB") {
        var count: Int = 1
    }

    private var isFirst = true

    override fun onBeforeSleep() {
        val usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        logger?.debug("Used Memory: $usedMemory (${usedMemory.mb}MB), Total Memory: $totalMemory (${totalMemory.mb}MB)")
        if (usedMemory / totalMemory.toFloat() >= 0.8) {
            if(isFirst) {
                isMemoryThresholdReached = true
            }
            if (!isMemoryThresholdReached && configuration.isMemoryWarningEnabled) {
                configuration.logger?.debug("Memory threshold reached")
                isMemoryThresholdReached = true
                if (memoryWarningException == null) {
                    memoryWarningException = MemoryWarningException(usedMemory.mb, totalMemory.mb)
                } else {
                    memoryWarningException?.count = (memoryWarningException?.count ?: 1) + 1
                }
            }
        } else {
            isMemoryThresholdReached = false
        }
        updateMemory(usedMemory)
        isFirst = false
    }

    private fun onThresholdReached(
        timer: Timer,
        memoryWarningException: MemoryWarningException
    ) {
        configuration.logger?.debug("Memory Warning received ${memoryWarningException.count} times: Used: ${memoryWarningException.usedMemory}MB, Total: ${memoryWarningException.totalMemory}MB")

        val timeStamp = System.currentTimeMillis().toString()
        val crashHitsTimer: Timer = Timer().startWithoutPerformanceMonitor()
        crashHitsTimer.setPageName(timer.getField(FIELD_PAGE_NAME)?: Tracker.BTErrorType.MemoryWarning.value)
        crashHitsTimer.nativeAppProperties = timer.nativeAppProperties
        crashHitsTimer.nativeAppProperties.add(deviceInfoProvider.getDeviceInfo())
        crashHitsTimer.setError(true)

        try {
            val thread = Thread(
                CrashRunnable(
                    configuration,
                    memoryWarningException.message ?: "",
                    timeStamp,
                    crashHitsTimer,
                    Tracker.BTErrorType.MemoryWarning,
                    errorCount = memoryWarningException.count,
                    deviceInfoProvider = deviceInfoProvider
                )
            )
            thread.start()
            thread.join()
        } catch (interruptedException: InterruptedException) {
            interruptedException.printStackTrace()
        }
    }

    private fun updateMemory(memory: Long) {
        if (memory < minMemory) {
            minMemory = memory
        }
        if (memory > maxMemory) {
            maxMemory = memory
        }
        if(isVerboseDebug) {
            memoryUsed.add(memory)
        }
        cumulativeMemory += memory
        memoryCount++
    }

    override fun onAfterSleep() {

    }

    override fun onTimerSubmit(timer: Timer) {
        super.onTimerSubmit(timer)
        memoryWarningException?.let {
            onThresholdReached(timer, it)
        }
        if (isVerboseDebug) {
            val pageName = timer.getField(FIELD_PAGE_NAME)?:""
            logger?.log(Log.VERBOSE, "Memory Samples for $pageName : $memoryUsed")
        }
    }
}