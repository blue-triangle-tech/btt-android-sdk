package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.Timer

object FirstTimerNameStrategy: GroupNamingStrategy {
    override fun getName(timers: List<Timer>): String {
        return timers[0].getField(Timer.FIELD_PAGE_NAME) + " Group"
    }
}