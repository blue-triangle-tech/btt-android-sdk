package com.bluetriangle.analytics.performancemonitor.accumulators

import com.bluetriangle.analytics.performancemonitor.DataPoint
import com.bluetriangle.analytics.performancemonitor.PerformanceMetric

internal class MemoryDataAccumulator(val isVerboseDebug: Boolean): MetricDataAccumulator<DataPoint.MemoryDataPoint> {

    private val totalMemory = Runtime.getRuntime().maxMemory()

    override val fields: Map<PerformanceMetric, String>
        get() = mapOf(
            PerformanceMetric.MinMemory to minMemory,
            PerformanceMetric.MaxMemory to maxMemory,
            PerformanceMetric.TotalMemory to totalMemory,
            PerformanceMetric.AvgMemory to avgMemory
        ).mapValues { it.value.toString() }

    private var minMemory = Long.MAX_VALUE
    private var maxMemory: Long = 0
    private var cumulativeMemory: Long = 0
    private var memoryCount: Long = 0
    var memoryUsed = arrayListOf<Long>()

    private val avgMemory: Long
        get() = if (memoryCount == 0L) 0 else cumulativeMemory / memoryCount

    override fun accumulate(data: DataPoint.MemoryDataPoint) {
        if (data.memoryUsage < minMemory) {
            minMemory = data.memoryUsage
        }
        if (data.memoryUsage > maxMemory) {
            maxMemory = data.memoryUsage
        }
        if (isVerboseDebug) {
            memoryUsed.add(data.memoryUsage)
        }
        cumulativeMemory += data.memoryUsage
        memoryCount++
    }

}