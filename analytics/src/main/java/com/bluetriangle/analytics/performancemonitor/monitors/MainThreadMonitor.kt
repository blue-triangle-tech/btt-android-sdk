package com.bluetriangle.analytics.performancemonitor.monitors

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.performancemonitor.DataPoint

internal class MainThreadMonitor(configuration: BlueTriangleConfiguration) : MetricMonitor {

    private val handler = Handler(Looper.getMainLooper())
    private var dummyTask = Runnable { }
    private var postTime: Long = 0L

    override fun setupMetric() {
        if (!handler.hasMessages(0)) {
            postTime = System.currentTimeMillis()
            handler.postAtFrontOfQueue(dummyTask)
        }
    }

    override fun captureDataPoint(): DataPoint {
        val threadBlockDelay = System.currentTimeMillis() - postTime

        return DataPoint.MainThreadDataPoint(threadBlockDelay)
    }
}