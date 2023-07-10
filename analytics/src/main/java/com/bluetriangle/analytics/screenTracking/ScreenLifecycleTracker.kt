package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.model.Screen

internal interface ScreenLifecycleTracker {

    fun onLoadStarted(screen: Screen)

    fun onLoadEnded(screen: Screen)

    fun onViewStarted(screen: Screen)

    fun onViewEnded(screen: Screen)

}