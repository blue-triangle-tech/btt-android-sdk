package com.bluetriangle.analytics.performancemonitor.monitors

import com.bluetriangle.analytics.performancemonitor.DataPoint

internal interface MetricMonitor {
    fun setupMetric()

    fun captureDataPoint(): DataPoint?

}