package com.bluetriangle.analytics.performancemonitor.monitors

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Timer.Companion.FIELD_PAGE_NAME
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider
import com.bluetriangle.analytics.performancemonitor.DataPoint
import kotlinx.coroutines.GlobalScope

internal class MemoryMonitor(
    val configuration: BlueTriangleConfiguration,
    private val deviceInfoProvider: IDeviceInfoProvider
) : MetricMonitor {

    private val totalMemory = Runtime.getRuntime().maxMemory()
    private val logger = configuration.logger

    private var isMemoryThresholdReached = false

    private val Long.mb: Long
        get() = this / (1024 * 1024)

    class MemoryWarningException(val usedMemory: Long, val totalMemory: Long) :
        RuntimeException("Critical memory usage detected. App using ${usedMemory}MB of App's limit ${totalMemory}MB") {
        var count: Int = 1
    }

    private var isFirst = true

    override fun setupMetric() {

    }

    private fun onThresholdReached(
        timer: Timer?,
        memoryWarningException: MemoryWarningException
    ) {
        configuration.logger?.debug("Memory Warning received ${memoryWarningException.count} times: Used: ${memoryWarningException.usedMemory}MB, Total: ${memoryWarningException.totalMemory}MB")

        val timeStamp = System.currentTimeMillis().toString()

        try {
            val thread = Thread(
                CrashRunnable(
                    configuration,
                    memoryWarningException.message ?: "",
                    timeStamp,
                    Tracker.BTErrorType.MemoryWarning,
                    mostRecentTimer = timer,
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

    override fun captureDataPoint(): DataPoint {
        val usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        logger?.debug("Used Memory: $usedMemory (${usedMemory.mb}MB), Total Memory: $totalMemory (${totalMemory.mb}MB)")
        if (usedMemory / totalMemory.toFloat() >= 0.8) {
            if(isFirst) {
                isMemoryThresholdReached = true
            }
            if (!isMemoryThresholdReached && configuration.isMemoryWarningEnabled) {
                configuration.logger?.debug("Memory threshold reached")
                isMemoryThresholdReached = true
                onThresholdReached(Tracker.instance?.getMostRecentTimer(), MemoryWarningException(usedMemory.mb, totalMemory.mb))
            }
        } else {
            isMemoryThresholdReached = false
        }
        isFirst = false
        return DataPoint.MemoryDataPoint(usedMemory)
    }

}