package com.bluetriangle.analytics.screenTracking

import android.util.Log
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.utility.logD

internal class BTTScreenLifecyleTracker : ScreenLifecycleTracker {

    val screenLogs = arrayListOf<ScreenTrackLog>()
    private val timers = hashMapOf<String, Timer>()

    data class ScreenTrackLog(
        val screenName: String,
        val startTime: Long,
        val timeTaken: Long,
        val isLoadEvent: Boolean = true
    )

    override fun onLoadStarted(screen: Screen) {
        timers[screen.name] = Timer(screen.name, "FragmentTrafficSegment").start()
        logD("BTTScreenLifecyleTracker", "onLoadStarted: ${screen.name}")
    }

    override fun onLoadEnded(screen: Screen) {
        logD("BTTScreenLifecyleTracker", "onLoadEnded: ${screen.name}")
        val screenName = screen.name
        val timer = timers[screenName] ?: return
        val timeTaken = System.currentTimeMillis() - timer.start
        screenLogs.add(ScreenTrackLog(screenName, timer.start, timeTaken, true))
        logD("BTTScreenLifecyleTracker", "$screenName loaded in $timeTaken ms")
    }

    override fun onViewStarted(screen: Screen) {
        logD("BTTScreenLifecyleTracker", "onViewStarted: ${screen.name}")
        timers[screen.name]?.visible()
    }

    override fun onViewEnded(screen: Screen) {
        logD("BTTScreenLifecyleTracker", "onViewEnded: ${screen.name}")
        val screenName = screen.name
        val timer = timers[screenName] ?: return
        val timeTaken = System.currentTimeMillis() - timer.visible
        screenLogs.add(ScreenTrackLog(screenName, timer.start, timeTaken, false))
        logD("BTTScreenLifecyleTracker", "$screenName viewed for $timeTaken ms")
        timer.end().submit()
        timers.remove(screenName)
    }

}