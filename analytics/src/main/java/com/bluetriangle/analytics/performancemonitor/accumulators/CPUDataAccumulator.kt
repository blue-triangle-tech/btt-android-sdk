package com.bluetriangle.analytics.performancemonitor.accumulators

import com.bluetriangle.analytics.performancemonitor.DataPoint
import com.bluetriangle.analytics.performancemonitor.PerformanceMetric

internal class CPUDataAccumulator(): MetricDataAccumulator<DataPoint.CPUDataPoint> {

    override val fields: Map<PerformanceMetric, String>
        get() = mapOf(
            PerformanceMetric.MinCpu to minCpu,
            PerformanceMetric.MaxCpu to maxCpu,
            PerformanceMetric.AvgCpu to avgCpu
        ).mapValues { it.value.toString() }

    private var minCpu = Double.MAX_VALUE
    private var maxCpu = 0.0
    private var cumulativeCpu = 0.0
    private var cpuCount: Long = 0
    private val avgCpu: Double
        get() = if (cpuCount == 0L) 0.0 else cumulativeCpu / cpuCount

    override fun accumulate(data: DataPoint.CPUDataPoint) {
        if (data.cpuUsage < minCpu) {
            minCpu = data.cpuUsage
        }
        if (data.cpuUsage > maxCpu) {
            maxCpu = data.cpuUsage
        }
        cumulativeCpu += data.cpuUsage
        cpuCount++
    }

}