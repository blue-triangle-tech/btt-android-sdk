package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Constants.TIMER_MIN_PGTM
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.screenTracking.grouping.BTTTimerGroupManager
import com.bluetriangle.analytics.model.ScreenType
import com.bluetriangle.analytics.utility.logD

internal class BTTScreenLifecycleTracker(
    private val screenTrackingEnabled: Boolean,
    var groupingEnabled: Boolean,
    idleTime: Int,
    internal var ignoreScreens: List<String>
) : ScreenLifecycleTracker {

    private var loadTime = hashMapOf<String, Long>()
    private var viewTime = hashMapOf<String, Long>()
    private val timers = hashMapOf<String, Timer>()
    private val TAG = this::class.java.simpleName

    var groupIdleTime = idleTime
        set(value) {
            field = value
            groupManager.groupIdleTime = value
        }

    private val groupManager = BTTTimerGroupManager(groupIdleTime)

    companion object {
        const val AUTOMATED_TIMERS_PAGE_TYPE = "ScreenTracker"
    }

    fun grouped(automated: Boolean): Boolean = groupingEnabled && automated

    override fun destroy() {
        groupManager.destroy()
    }

    override fun onLoadStarted(screen: Screen, automated: Boolean) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.pageName(grouped(automated)))) return

        logD(TAG, "onLoadStarted: $screen")
        createTimerAndCaptureLoadTime(screen, grouped(automated))
    }

    override fun onLoadEnded(screen: Screen, automated: Boolean) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.pageName(grouped(automated)))) return

        timers[screen.toString()]?.setPageName(screen.pageName(grouped(automated)))
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

        if(grouped(automated)) {
            screen.onTitleUpdated = {
                timers[screen.toString()]?.setPageName(screen.pageName(grouped(automated)))
            }
        }
    }

    @Synchronized
    private fun createTimerAndCaptureLoadTime(screen: Screen, isGrouped: Boolean) {
        val timer = Timer(screen.pageName(isGrouped), AUTOMATED_TIMERS_PAGE_TYPE)
        timers[screen.toString()] = timer
        if(isGrouped) {
            groupManager.add(screen, timer)
            timer.startSilent()
        } else {
            timer.start()
        }
        loadTime[screen.toString()] = System.currentTimeMillis()
    }

    override fun onViewEnded(screen: Screen, automated: Boolean) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.pageName(grouped(automated)))) return

        logD(TAG, "onViewEnded: $screen")
        val scr = screen.toString()
        val timer = timers[scr] ?: return

        generateMetaData(screen, timer)

        if(grouped(automated)) {
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

        var confidenceRate = 100
        var confidenceMsg: String? = null

        var pgTm = viewTm - loadTm

        if(loadTm == 0L) {
            confidenceRate = 0
            confidenceMsg = when(screen.type) {
                ScreenType.Activity, ScreenType.Fragment -> "onCreate/onStart not called on ${screen.name}"
                ScreenType.Composable -> "Composable load time could not be calculated"
                is ScreenType.Custom -> "onLoadStarted/onLoadEnded methods were not called"
            }
        } else if(viewTm == 0L) {
            confidenceRate = 0
            confidenceMsg = when(screen.type) {
                ScreenType.Activity, ScreenType.Fragment -> "onResume not called on ${screen.name}"
                ScreenType.Composable -> "Composable load time could not be calculated"
                is ScreenType.Custom -> "onViewStarted method was not called"
            }
        } else if(pgTm > 20_000) {
            pgTm = 20_000
            confidenceRate = 50
            confidenceMsg = "load time calculation gone out of bounds"
        }
        val disappearTm = System.currentTimeMillis()

        timer.setContentGroupName(AUTOMATED_TIMERS_PAGE_TYPE)
        timer.pageTimeCalculator = {
            (pgTm).coerceAtLeast(TIMER_MIN_PGTM)
        }

        timer.generateNativeAppProperties()

        timer.nativeAppProperties.loadStartTime = loadTm
        timer.nativeAppProperties.loadEndTime = viewTm
        timer.nativeAppProperties.disappearTime = disappearTm
        timer.nativeAppProperties.className = screen.name

        timer.nativeAppProperties.loadTime = viewTm - loadTm
        timer.nativeAppProperties.loadTime = pgTm
        timer.nativeAppProperties.fullTime = disappearTm - loadTm
        timer.nativeAppProperties.screenType = screen.type
        timer.nativeAppProperties.confidenceRate = confidenceRate
        timer.nativeAppProperties.confidenceMsg = confidenceMsg
    }

    private fun shouldIgnore(name: String): Boolean {
        return ignoreScreens.contains(name)
    }

    override fun setGroupName(groupName: String) {
        if(!groupingEnabled) return

        groupManager.setGroupName(groupName)
    }

    override fun setNewGroup(groupName: String) {
        if(!groupingEnabled) return

        groupManager.setNewGroup(groupName)
    }

}