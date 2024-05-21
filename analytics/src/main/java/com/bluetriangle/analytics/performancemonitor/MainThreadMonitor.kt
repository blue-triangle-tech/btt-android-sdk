package com.bluetriangle.analytics.performancemonitor

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.BlueTriangleConfiguration

internal class MainThreadMonitor(configuration: BlueTriangleConfiguration) : MetricMonitor {
    override val metricFields: Map<String, String>
        get() = mapOf()

    private val handler = Handler(Looper.getMainLooper())
    private var dummyTask = Runnable { }
    private var postTime: Long = 0L

    internal var maxMainThreadBlock: Long = 0L

    override fun onBeforeSleep() {
        if (!handler.hasMessages(0)) {
            postTime = System.currentTimeMillis()
            handler.postAtFrontOfQueue(dummyTask)
        }
    }

    override fun onAfterSleep() {
        val threadBlockDelay = System.currentTimeMillis() - postTime

        maxMainThreadBlock = maxMainThreadBlock.coerceAtLeast(threadBlockDelay)
    }
}