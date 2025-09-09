package com.bluetriangle.analytics.performancemonitor

sealed class PerformanceMetric(val analyticsField: String, val wcdField: String) {
    object MinCpu: PerformanceMetric("minCPU", "CPU_MIN")
    object MaxCpu: PerformanceMetric("maxCPU", "CPU_MAX")
    object AvgCpu: PerformanceMetric("avgCPU", "CPU_AVG")
    object MinMemory: PerformanceMetric("minMemory", "MEMORY_MIN")
    object TotalMemory: PerformanceMetric("totalMemory", "MEMORY_TOTAL")
    object MaxMemory: PerformanceMetric("maxMemory", "MEMORY_MAX")
    object AvgMemory: PerformanceMetric("avgMemory", "MEMORY_AVG")
}