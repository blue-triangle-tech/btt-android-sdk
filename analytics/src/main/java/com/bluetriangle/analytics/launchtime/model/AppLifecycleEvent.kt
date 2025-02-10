package com.bluetriangle.analytics.launchtime.model

internal sealed class AppLifecycleEvent(val time:Long) {
    class AppLifecycleCreated: AppLifecycleEvent(System.currentTimeMillis())
    class ActivityCreated : AppLifecycleEvent(System.currentTimeMillis())
    class ActivityStarted: AppLifecycleEvent(System.currentTimeMillis())
    class ActivityResumed: AppLifecycleEvent(System.currentTimeMillis())
}