package com.bluetriangle.analytics.screenTracking.grouping

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker

class BTTTimerGroup(
    private val namingStrategy: GroupNamingStrategy = FirstTimerNameStrategy,
    private val onCompleted: (BTTTimerGroup)-> Unit
) {
    private var timers = mutableListOf<Timer>()
    private var groupTimer = Timer()
    var isClosed = false
        private set
    private var isSubmitted = false
    private var logger = Tracker.instance?.configuration?.logger
    private val idleTime = 5000L


    private var handler = Handler(Looper.getMainLooper())
    private val closeGroupRunnable: Runnable = Runnable {
        close()
    }

    private fun resetIdleTimeout() {
        handler.removeCallbacks(closeGroupRunnable)
        handler.postDelayed(closeGroupRunnable, idleTime)
    }

    init {
        groupTimer.start()
        resetIdleTimeout()
    }

    fun add(timer: Timer) {
        if(isClosed) {
            logger?.info("Tried to add timer to closed group.")
            return
        }

        timers.add(timer)
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
        groupTimer.submit()
    }

    fun flush() {
        timers.forEach {
            it.end()
        }
        timers.clear()
    }
}