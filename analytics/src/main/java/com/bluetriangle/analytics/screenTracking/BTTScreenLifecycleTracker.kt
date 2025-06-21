package com.bluetriangle.analytics.screenTracking

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bluetriangle.analytics.Constants.TIMER_MIN_PGTM
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.screenTracking.grouping.BTTTimerGroupManager
import com.bluetriangle.analytics.utility.logD

internal class BTTScreenLifecycleTracker(
    private val screenTrackingEnabled: Boolean,
    private val groupingEnabled: ()-> Boolean,
    groupDecayInSecs: () -> Int,
    internal var ignoreScreens: List<String>
) : ScreenLifecycleTracker {

    private var loadTime = hashMapOf<String, Long>()
    private var viewTime = hashMapOf<String, Long>()
    private val timers = hashMapOf<String, Timer>()
    private val TAG = this::class.java.simpleName
    private val groupManager = BTTTimerGroupManager(groupDecayInSecs)

    fun setScreenName(screenName: String) {
        groupManager.setScreenName(screenName)
    }

    fun grouped(automated: Boolean): Boolean = groupingEnabled() && automated

    override fun onLoadStarted(screen: Screen, automated: Boolean) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.pageName(grouped(automated)))) return

        logD(TAG, "onLoadStarted: $screen")
        createTimerAndCaptureLoadTime(screen, grouped(automated))
    }

    override fun onLoadEnded(screen: Screen, automated: Boolean) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.pageName(grouped(automated)))) return

        timers[screen.toString()]?.setPageName(screen.pageName(groupingEnabled()))
        logD(TAG, "onLoadEnded: $screen")
        if (timers[screen.toString()] == null) {
            createTimerAndCaptureLoadTime(screen, grouped(automated))
        }
    }

    override fun onViewStarted(screen: Screen, automated: Boolean) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.pageName(grouped(automated)))) return

        if (timers[screen.toString()] == null) {
            createTimerAndCaptureLoadTime(screen, grouped(automated))
        }
        logD(TAG, "onViewStarted: $screen")
        viewTime[screen.toString()] = System.currentTimeMillis()

        if(groupingEnabled()) {
            Handler(Looper.getMainLooper()).postDelayed({
                timers[screen.toString()]?.setPageName(screen.pageName(groupingEnabled()))
            }, 400)
        }
    }

    @Synchronized
    private fun createTimerAndCaptureLoadTime(screen: Screen, isGrouped: Boolean) {
        val timer = Timer(screen.pageName(isGrouped), "ScreenTracker")
        timers[screen.toString()] = timer
        if(groupingEnabled()) {
            groupManager.add(screen, timer)
            timer.startDummy()
        } else {
            timer.start()
        }
        loadTime[screen.toString()] = System.currentTimeMillis()
    }

    override fun onViewEnded(screen: Screen, automated: Boolean) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.pageName(grouped(automated)))) return

        logD(TAG, "onViewEnded: $screen")
        Log.d("ToolbarTitleExperiment", "screen: ${screen} : ${screen.title}")
        val scr = screen.toString()
        val timer = timers[scr] ?: return

        generateMetaData(screen, timer)

        if(groupingEnabled()) {
            timer.end()
        } else {
            timer.end().submit()
        }
        timers.remove(scr)
    }

    override fun generateMetaData(screen: Screen, timer: Timer) {
        val scr  = screen.toString()
        val loadTm = loadTime[scr] ?: 0L
        val viewTm = viewTime[scr] ?: 0L
        val disappearTm = System.currentTimeMillis()

        timer.pageTimeCalculator = {
            (viewTm - loadTm).coerceAtLeast(TIMER_MIN_PGTM)
        }

        timer.generateNativeAppProperties()

        timer.nativeAppProperties.loadStartTime = loadTm
        timer.nativeAppProperties.loadEndTime = viewTm
        timer.nativeAppProperties.disappearTime = disappearTm
        timer.nativeAppProperties.className = screen.name

        timer.nativeAppProperties.loadTime = viewTm - loadTm
        timer.nativeAppProperties.fullTime = disappearTm - loadTm
        timer.nativeAppProperties.screenType = screen.type
    }

    private fun shouldIgnore(name: String): Boolean {
        return ignoreScreens.contains(name)
    }

}