package com.bluetriangle.analytics.performancemonitor

import android.util.Log
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Timer.Companion.FIELD_PAGE_NAME
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.performancemonitor.accumulators.CPUDataAccumulator
import com.bluetriangle.analytics.performancemonitor.accumulators.MemoryDataAccumulator
import java.util.UUID

internal class PerformanceSpan(configuration: BlueTriangleConfiguration) : PerformanceListener {

    val id = UUID.randomUUID().toString()

    val logger = configuration.logger

    var isVerboseDebug = configuration.isDebug && configuration.debugLevel == Log.VERBOSE

    private var memoryData: MemoryDataAccumulator? = null

    private var cpuData: CPUDataAccumulator? = null

    internal var maxMainThreadUsage: Long = 0L

    internal val performanceFields: Map<PerformanceMetric, String>
        get() = buildMap {
            memoryData?.let {
                putAll(it.fields)
            }
            cpuData?.let {
                putAll(it.fields)
            }
        }

    fun start() {
        Tracker.instance?.performanceMonitor?.registerListener(this)
    }

    fun stop() {
        Tracker.instance?.performanceMonitor?.unregisterListener(this)
    }

    override fun onDataReceived(data: List<DataPoint?>) {
        data.forEach {
            if (it != null) {
                when (it) {
                    is DataPoint.CPUDataPoint -> {
                        if(cpuData == null) {
                            cpuData = CPUDataAccumulator(isVerboseDebug)
                        }
                        cpuData?.accumulate(it)
                    }
                    is DataPoint.MainThreadDataPoint -> updateMainThreadUsage(it)
                    is DataPoint.MemoryDataPoint -> {
                        if(memoryData == null) {
                            memoryData = MemoryDataAccumulator(isVerboseDebug)
                        }
                        memoryData?.accumulate(it)
                    }
                }
            }
        }
    }

    private fun updateMainThreadUsage(mainThreadUsageData: DataPoint.MainThreadDataPoint) {
        maxMainThreadUsage = maxMainThreadUsage.coerceAtLeast(mainThreadUsageData.mainThreadUsage)
    }

    fun onTimerSubmit(timer: Timer) {
        if (isVerboseDebug) {
            val pageName = timer.getField(FIELD_PAGE_NAME)
            cpuData?.let {
                logger?.log(Log.VERBOSE, "CPU Samples for $pageName : ${it.cpuUsed}")
            }
            memoryData?.let {
                logger?.log(Log.VERBOSE, "Memory Samples for $pageName : ${it.memoryUsed}")
            }
        }
    }
}
