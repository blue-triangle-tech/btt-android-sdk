package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.Timer

interface GroupNamingStrategy {

    fun getName(timers: List<Timer>): String
}