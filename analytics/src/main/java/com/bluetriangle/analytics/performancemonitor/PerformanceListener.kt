package com.bluetriangle.analytics.performancemonitor

interface PerformanceListener {
    fun onDataReceived(data: List<DataPoint?>)
}

sealed class DataPoint {
    class CPUDataPoint(val cpuUsage: Double): DataPoint()
    class MemoryDataPoint(val memoryUsage: Long): DataPoint()

    class MainThreadDataPoint(val mainThreadUsage: Long): DataPoint()
}