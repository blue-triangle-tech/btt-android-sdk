package com.bluetriangle.analytics.screenTracking

internal interface ScreenLifecycleTracker {

    fun onLoadStarted(screen: Screen)

    fun onLoadEnded(screen: Screen)

    fun onViewStarted(screen: Screen)

    fun onViewEnded(screen: Screen)

}