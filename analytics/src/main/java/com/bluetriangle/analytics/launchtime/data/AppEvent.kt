package com.bluetriangle.analytics.launchtime.data

import android.os.Bundle

sealed class AppEvent(val time:Long) {
    class AppCreated: AppEvent(System.currentTimeMillis())
    class ActivityCreated(data:Bundle?): AppEvent(System.currentTimeMillis())
    class ActivityStarted: AppEvent(System.currentTimeMillis())
    class ActivityResumed: AppEvent(System.currentTimeMillis())
}