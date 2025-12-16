package com.bluetriangle.analytics.performancemonitor

enum class PerformanceMetric(val field: String) {
    MinCpu("minCPU"),
    MaxCpu("maxCPU"),
    AvgCpu("avgCPU"),
    MinMemory("minMemory"),
    TotalMemory("totalMemory"),
    MaxMemory("maxMemory"),
    AvgMemory("avgMemory")
}