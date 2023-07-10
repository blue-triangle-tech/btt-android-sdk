package com.bluetriangle.analytics.monitor

internal interface MetricMonitor {

    val metricFields: Map<String, String>

    fun onBeforeSleep()

    fun onAfterSleep()

}