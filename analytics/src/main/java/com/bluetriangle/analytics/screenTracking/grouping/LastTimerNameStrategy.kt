package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.Timer

object LastTimerNameStrategy: GroupNamingStrategy {
    override fun getName(timers: List<Timer>): String {
        return (timers.lastOrNull()?.getField(Timer.FIELD_PAGE_NAME)?:"")
    }
}