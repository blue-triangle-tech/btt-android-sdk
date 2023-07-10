package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.utility.logD

internal class BTTScreenLifecyleTracker(private val screenTrackingEnabled: Boolean) : ScreenLifecycleTracker {

    private var loadTime = hashMapOf<String, Long>()
    private var viewTime = hashMapOf<String, Long>()
    private val timers = hashMapOf<String, Timer>()
    private val TAG = this::class.java.simpleName

    override fun onLoadStarted(screen: Screen) {
        if(!screenTrackingEnabled) return
        logD(TAG, "onLoadStarted: ${screen.name}")
        timers[screen.toString()] = Timer(screen.name, "ScreenTracker").start()
        loadTime[screen.toString()] = System.currentTimeMillis()
    }

    override fun onLoadEnded(screen: Screen) {
        if(!screenTrackingEnabled) return
        logD(TAG, "onLoadEnded: ${screen.name}")
        if(timers[screen.toString()] == null) {
            timers[screen.toString()] = Timer(screen.name, "ScreenTracker").start()
            loadTime[screen.toString()] = System.currentTimeMillis()
        }
    }

    override fun onViewStarted(screen: Screen) {
        if(!screenTrackingEnabled) return
        logD(TAG, "onViewStarted: ${screen.name}")
        viewTime[screen.toString()] = System.currentTimeMillis()
    }

    override fun onViewEnded(screen: Screen) {
        if(!screenTrackingEnabled) return
        logD(TAG, "onViewEnded: ${screen.name}")
        val scr = screen.toString()
        val timer = timers[scr] ?: return
        val loadTm = loadTime[scr]?:0L
        val viewTm = viewTime[scr]?:0L
        val disappearTm = System.currentTimeMillis()

        timer.pageTimeCalculator = {
            viewTm - loadTm
        }
        timer.generateNativeAppProperties()
        timer.nativeAppProperties.loadTime = viewTm - loadTm
        timer.nativeAppProperties.fullTime = disappearTm - loadTm
        timer.nativeAppProperties.screenType = screen.type
        timer.end().submit()
        timers.remove(scr)
    }

}