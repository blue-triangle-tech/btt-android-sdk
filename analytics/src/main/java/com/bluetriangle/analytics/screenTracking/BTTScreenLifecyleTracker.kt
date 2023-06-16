package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.NativeAppProperties
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.utility.logD

internal class BTTScreenLifecyleTracker : ScreenLifecycleTracker {

    private var loadTime = hashMapOf<String, Long>()
    private var viewTime = hashMapOf<String, Long>()
    private var disappearTime = hashMapOf<String, Long>()
    private val timers = hashMapOf<String, Timer>()
    private val TAG = this::class.java.simpleName

    override fun onLoadStarted(screen: Screen) {
        logD(TAG, "onLoadStarted: ${screen.name}")
        timers[screen.toString()] = Timer(screen.name, "FragmentTrafficSegment").start()
        loadTime[screen.toString()] = System.currentTimeMillis()
    }

    override fun onLoadEnded(screen: Screen) {
        logD(TAG, "onLoadEnded: ${screen.name}")
    }

    override fun onViewStarted(screen: Screen) {
        logD(TAG, "onViewStarted: ${screen.name}")
        viewTime[screen.toString()] = System.currentTimeMillis()
    }

    override fun onViewEnded(screen: Screen) {
        logD(TAG, "onViewEnded: ${screen.name}")
        val scr = screen.toString()
        val timer = timers[scr] ?: return
        val loadTm = loadTime[scr]?:0L
        val viewTm = viewTime[scr]?:0L
        val disappearTm = disappearTime[scr]?:0L

        timer.pageTimeCalculator = {
            viewTm - loadTm
        }
        timer.nativeAppProperties = NativeAppProperties(
            viewTm - loadTm,
            disappearTm - loadTm,
            0L,
            screen.type
        )
        timer.end().submit()
        timers.remove(scr)
    }

}