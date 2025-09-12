package com.bluetriangle.analytics.performancemonitor

sealed class PerformanceMetric(val field: String) {
    object MinCpu: PerformanceMetric("minCPU")
    object MaxCpu: PerformanceMetric("maxCPU")
    object AvgCpu: PerformanceMetric("avgCPU")
    object MinMemory: PerformanceMetric("minMemory")
    object TotalMemory: PerformanceMetric("totalMemory")
    object MaxMemory: PerformanceMetric("maxMemory")
    object AvgMemory: PerformanceMetric("avgMemory")
}