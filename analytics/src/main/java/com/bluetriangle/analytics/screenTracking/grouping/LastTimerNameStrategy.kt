package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.Timer

object LastTimerNameStrategy: GroupNamingStrategy {
    override fun getName(timers: List<Timer>): String {
        return timers.last().getField(Timer.FIELD_PAGE_NAME) + " Group of ${timers.size} Views"
    }
}