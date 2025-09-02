package com.bluetriangle.analytics.screenTracking.grouping

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.Constants.TIMER_MIN_PGTM
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.screenTracking.BTTScreenLifecycleTracker.Companion.AUTOMATED_TIMERS_PAGE_TYPE

sealed class GroupingCause(val name: String, val timeInterval: Long) {
    class Timeout(timeInterval: Long): GroupingCause("timeout", timeInterval)
    class Tap(timeInterval: Long): GroupingCause("tap", timeInterval)
    class Manual(timeInterval: Long): GroupingCause("manual", timeInterval)
}

internal class BTTTimerGroup(
    private val namingStrategy: GroupNamingStrategy = LastTimerNameStrategy,
    groupIdleTime: Int,
    private val onCompleted: (BTTTimerGroup)-> Unit,
    private val groupingCause: GroupingCause
) {
    private var timers = mutableListOf<Pair<Screen, Timer>>()
    private var groupTimer = Timer()
    var isClosed = false
        private set
    var isSubmitted = false
        private set
    private var logger = Tracker.instance?.configuration?.logger
    private val idleTime = groupIdleTime * 1000L

    private var manualGroupName: String? = null

    private var handler = Handler(Looper.getMainLooper())
    private val closeGroupRunnable: Runnable = Runnable {
        close()
    }

    val startTime: Long
        get() = groupTimer.start

    private var groupName: String? = null

    fun setGroupName(name: String) {
        this.groupName = name
        if(manualGroupName == null) {
            groupTimer.setPageName(name)
        }
    }

    fun setManualGroupName(groupName: String) {
        this.manualGroupName = groupName
        groupTimer.setPageName(groupName)
    }

    private fun resetIdleTimeout() {
        handler.removeCallbacks(closeGroupRunnable)
        handler.postDelayed(closeGroupRunnable, idleTime)
    }

    init {
        logger?.debug("Group Started.. ${this.hashCode()}")
        groupTimer.start()
        groupTimer.setTrafficSegmentName(AUTOMATED_TIMERS_PAGE_TYPE)
        groupTimer.setContentGroupName(AUTOMATED_TIMERS_PAGE_TYPE)
        resetIdleTimeout()
    }

    fun add(screen: Screen, timer: Timer) {
        if(isClosed) {
            logger?.info("Tried to add timer to closed group.")
            return
        }

        if(groupName == null && manualGroupName == null) {
            groupTimer.setPageName(screen.name)
        }

        timers.add(screen to timer)
        observeTimerEnd(timer)
        resetIdleTimeout()
    }

    private fun observeTimerEnd(timer: Timer) {
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

    fun end() {
        groupTimer.end()
    }

    fun submit() {
        val tracker = Tracker.instance?:return

        if(timers.isEmpty()) return

        val groupPageName = (manualGroupName ?: (groupName ?: namingStrategy.getName(timers.map { it.second })))
        groupTimer.setPageName(groupPageName)

        generateGroupProperties(tracker)
        groupTimer.submit()
        logger?.debug("Group Submitted.. ${this.hashCode()}")
        if(tracker.configuration.shouldSampleGroupedView) {
            tracker.trackerExecutor.submit(
                GroupChildRunnable(tracker.configuration, groupTimer, childViews = mapTimersToChildViews())
            )
        }
    }

    private fun mapTimersToChildViews(): List<BTTChildView> {
        val groupStartTime = groupTimer.start

        return timers.map {
            val childPageName = it.second.getField(Timer.FIELD_PAGE_NAME)?:""
            val childPgTm = it.second.pageTimeCalculator().toString()
            val childNativeAppProp = it.second.nativeAppProperties
            val childLoadStartTime = it.second.start
            val childLoadEndTime = childNativeAppProp.loadEndTime

            BTTChildView(
                childNativeAppProp.className,
                childPageName,
                childPgTm,
                (childLoadStartTime - groupStartTime).toString(),
                (childLoadEndTime - groupStartTime).toString(),
                childNativeAppProp
            )
        }
    }

    private fun generateGroupProperties(tracker: Tracker) {
        timers.forEach {
            tracker.screenTrackMonitor?.generateMetaData(it.first, it.second)
            it.second.nativeAppProperties.grouped = true
        }

        groupTimer.generateNativeAppProperties()

        val loadStartTime = timers.minOfOrNull { it.second.nativeAppProperties.loadStartTime }?:0
        val disappearTm = timers.maxOfOrNull { it.second.nativeAppProperties.disappearTime }?:0
        val loadTime = calculateGroupPgTm(timers.map { it.second.nativeAppProperties.loadStartTime to it.second.nativeAppProperties.loadEndTime })

        groupTimer.pageTimeCalculator = {
            loadTime.coerceAtLeast(TIMER_MIN_PGTM)
        }
        groupTimer.nativeAppProperties.loadTime = loadTime
        groupTimer.nativeAppProperties.fullTime = disappearTm - loadStartTime
        groupTimer.nativeAppProperties.grouped = true
        groupTimer.nativeAppProperties.groupingCause = groupingCause.name
        groupTimer.nativeAppProperties.groupingCauseInterval = groupingCause.timeInterval
    }

    private fun calculateGroupPgTm(loadTimes: List<Pair<Long, Long>>): Long {
        val sortedTimes = loadTimes.sortedBy { it.first }
        val mergedTimes = mutableListOf<Pair<Long, Long>>(
            sortedTimes[0]
        )
        if(sortedTimes.size > 1) {
            for(i in 1 until sortedTimes.size) {
                val last = mergedTimes.last()
                val current = sortedTimes[i]
                if(current.first <= last.second) {
                    mergedTimes.removeAt(mergedTimes.lastIndex)
                    mergedTimes.add(last.first to maxOf(last.second, current.second))
                } else {
                    mergedTimes.add(current)
                }
            }
        }
        return mergedTimes.sumOf { it.second - it.first }
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