package com.bluetriangle.analytics.performancemonitor

import com.bluetriangle.analytics.Timer

internal interface MetricMonitor {

    val metricFields: Map<String, String>

    fun onBeforeSleep()

    fun onAfterSleep()

    fun onTimerSubmit(timer: Timer) {

    }

}