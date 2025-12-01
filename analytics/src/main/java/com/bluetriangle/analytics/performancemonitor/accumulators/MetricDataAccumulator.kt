package com.bluetriangle.analytics.performancemonitor.accumulators

import com.bluetriangle.analytics.performancemonitor.DataPoint
import com.bluetriangle.analytics.performancemonitor.PerformanceMetric

interface MetricDataAccumulator<T: DataPoint> {

    val fields: Map<PerformanceMetric, String>

    fun accumulate(data: T)

}