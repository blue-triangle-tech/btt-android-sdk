package com.bluetriangle.analytics.launchtime.data

sealed class AppEvent(val time:Long) {
    class AppCreated(time:Long = System.currentTimeMillis()): AppEvent(time)
    class ActivityStarted(time:Long = System.currentTimeMillis()): AppEvent(time)
    class ActivityResumed(time:Long = System.currentTimeMillis()): AppEvent(time)
}