package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.Screen

internal interface ScreenLifecycleTracker {

    fun onLoadStarted(screen: Screen, automated: Boolean)

    fun onLoadEnded(screen: Screen, automated: Boolean)

    fun onViewStarted(screen: Screen, automated: Boolean)

    fun onViewEnded(screen: Screen, automated: Boolean)

    fun generateMetaData(screen: Screen, timer: Timer)

}