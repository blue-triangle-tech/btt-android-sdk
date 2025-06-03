package com.bluetriangle.analytics.screenTracking.grouping

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker

class BTTTimerGroup2(
    private val namingStrategy: GroupNamingStrategy = LastTimerNameStrategy,
    groupDecayInSecs: Int,
    private val onCompleted: (BTTTimerGroup2)-> Unit
) {
    private var timers = mutableListOf<Timer>()
    private var groupTimer = Timer()
    var isClosed = false
        private set
    private val groupId = System.currentTimeMillis().toString()
    private var isSubmitted = false
    private var logger = Tracker.instance?.configuration?.logger

    private var handler = Handler(Looper.getMainLooper())
    private val closeGroupRunnable: Runnable = Runnable {
        close()
    }

    init {
        groupTimer.start()
        handler.postDelayed(closeGroupRunnable, groupDecayInSecs * 1000L)
    }

    fun add(timer: Timer) {
        if(isClosed) {
            logger?.info("Tried to add timer to closed group.")
            return
        }
        logger?.debug("Adding Timer to Group: ${timer.getField(Timer.FIELD_PAGE_NAME)} -> $groupId")

        timers.add(timer)
        observe(timer)
    }

    private fun observe(timer: Timer) {
        timer.onEnded = {
            logger?.info("Timer Ended: ${timer.getField(Timer.FIELD_PAGE_NAME)}")
            checkCompletion()
        }
    }

    private fun close() {
        if(isClosed) return

        isClosed = true
        handler.removeCallbacks(closeGroupRunnable)

        checkCompletion()
    }

    private fun checkCompletion() {
        if(!isClosed || isSubmitted) return

        val allEnded = timers.all { it.hasEnded() }

        if(allEnded) {
            isSubmitted = true

            onCompleted(this)
        }
    }

    fun submit() {
        val groupName = namingStrategy.getName(timers)
        groupTimer.setPageName(groupName)
        groupTimer.setTrafficSegmentName("ScreenTracker")
        groupTimer.generateNativeAppProperties()
        groupTimer.nativeAppProperties.childViews =
            timers.mapNotNull { it.getField(Timer.FIELD_PAGE_NAME) }
        groupTimer.nativeAppProperties.loadTime = timers.maxBy { it.nativeAppProperties.loadTime?:0L }.nativeAppProperties.loadTime
        groupTimer.submit()
    }

    fun flush() {
        timers.forEach {
            it.end()
        }
        timers.clear()
    }
}