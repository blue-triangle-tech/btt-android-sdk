package com.bluetriangle.analytics.screenTracking.grouping

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.Constants.TIMER_MIN_PGTM
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.model.Screen
import kotlin.concurrent.timer

internal class BTTTimerGroup(
    private val namingStrategy: GroupNamingStrategy = LastTimerNameStrategy,
    groupDecayInSecs: Int,
    private val onCompleted: (BTTTimerGroup)-> Unit
) {
    private var timers = mutableListOf<Pair<Screen, Timer>>()
    private var groupTimer = Timer()
    var isClosed = false
        private set
    var isSubmitted = false
        private set
    private var logger = Tracker.instance?.configuration?.logger
    private val idleTime = groupDecayInSecs * 1000L

    private var handler = Handler(Looper.getMainLooper())
    private val closeGroupRunnable: Runnable = Runnable {
        close()
    }

    private var screenName: String? = null

    fun setScreenName(name: String) {
        this.screenName = name
    }

    private fun resetIdleTimeout() {
        handler.removeCallbacks(closeGroupRunnable)
        handler.postDelayed(closeGroupRunnable, idleTime)
    }

    init {
        logger?.debug("Group Started.. ${this.hashCode()}")
        groupTimer.start()
        resetIdleTimeout()
    }

    fun add(screen: Screen, timer: Timer) {
        if(isClosed) {
            logger?.info("Tried to add timer to closed group.")
            return
        }

        timers.add(screen to timer)
        observe(timer)
        resetIdleTimeout()
    }

    private fun observe(timer: Timer) {
        timer.onEnded = {
            logger?.info("Timer Ended: ${timer.getField(Timer.FIELD_PAGE_NAME)}")
            checkCompletion()
        }
    }

    private fun close() {
        if(isClosed) return

        logger?.debug("Group Closed.. ${this.hashCode()}")
        isClosed = true
        handler.removeCallbacks(closeGroupRunnable)

        checkCompletion()
    }

    private fun checkCompletion() {
        if(!isClosed || isSubmitted) return

        val allEnded = timers.all { it.second.hasEnded() }

        if(allEnded) {
            isSubmitted = true

            onCompleted(this)
        }
    }

    fun submit() {
        val tracker = Tracker.instance?:return

        if(timers.isEmpty()) return

        timers.forEach {
            tracker.screenTrackMonitor?.generateMetaData(it.first, it.second)
        }
        val groupName = screenName ?: namingStrategy.getName(timers.map { it.second })
        groupTimer.setPageName(groupName)
        groupTimer.setTrafficSegmentName("ScreenTracker")
        groupTimer.generateNativeAppProperties()

        val loadStartTime = timers.minOfOrNull { it.second.nativeAppProperties.loadStartTime }?:0

        val overlapTime = calculateTotalOverlap(timers.map { it.second })
        val disappearTm = timers.maxOfOrNull { it.second.nativeAppProperties.disappearTime }?:0
        val loadTime = timers.sumOf { it.second.nativeAppProperties.loadTime?:0 } - overlapTime

        groupTimer.pageTimeCalculator = {
            loadTime.coerceAtLeast(TIMER_MIN_PGTM)
        }
        groupTimer.nativeAppProperties.loadTime = loadTime
        groupTimer.nativeAppProperties.fullTime = disappearTm - loadStartTime

        groupTimer.submit()
        logger?.debug("Group Submitted.. ${this.hashCode()}")
        tracker.trackerExecutor.submit(
            GroupChildRunnable(tracker.configuration, groupTimer, childViews = timers.map {
                BTTChildView(
                    it.second.nativeAppProperties.className,
                    it.second.getField(Timer.FIELD_PAGE_NAME)?:"",
                    it.second.pageTimeCalculator().toString(),
                    ((it.second.getField(Timer.FIELD_NST)?.toLong()?:0L) - (groupTimer.getField(Timer.FIELD_NST)?.toLong()?:0L)).toString(),
                    (it.second.nativeAppProperties.loadEndTime - (groupTimer.getField(Timer.FIELD_NST)?.toLong()?:0L)).toString()
                )
            })
        )
    }

    fun calculateTotalOverlap(timers: List<Timer>): Long {
        if (timers.isEmpty()) return 0L

        // Step 1: Convert to list of events
        data class Event(val time: Long, val isStart: Boolean)

        val events = mutableListOf<Event>()
        for (timer in timers) {
            events.add(Event(timer.nativeAppProperties.loadStartTime, true))  // Start of interval
            events.add(Event(timer.nativeAppProperties.loadEndTime, false))   // End of interval
        }

        // Step 2: Sort events by time; starts come before ends when time is equal
        events.sortWith(compareBy<Event> { it.time }.thenBy { if (it.isStart) 0 else 1 })

        // Step 3: Sweep line algorithm
        var activeCount = 0
        var lastTime: Long? = null
        var totalOverlap = 0L

        for (event in events) {
            if (lastTime != null && activeCount >= 2) {
                totalOverlap += event.time - lastTime
            }

            activeCount += if (event.isStart) 1 else -1
            lastTime = event.time
        }

        return totalOverlap
    }

    fun flush() {
        timers.forEach {
            it.second.onEnded = {}
        }
        timers.forEach {
            if(!it.second.hasEnded()) {
                it.second.end()
            }
        }
        timers.clear()
    }
}