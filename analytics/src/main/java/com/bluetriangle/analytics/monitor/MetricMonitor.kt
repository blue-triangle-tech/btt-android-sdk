package com.bluetriangle.analytics.monitor

interface MetricMonitor {

    val metricFields: Map<String, String>

    fun onBeforeSleep()

    fun onAfterSleep()

}