package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.Screen

internal interface ScreenLifecycleTracker {

    fun onLoadStarted(screen: Screen, automated: Boolean = false)

    fun onLoadEnded(screen: Screen, automated: Boolean = false)

    fun onViewStarted(screen: Screen, automated: Boolean = false)

    fun onViewEnded(screen: Screen, automated: Boolean = false)

    fun generateMetaData(screen: Screen, timer: Timer)

    fun setScreenName(screenName: String)

    fun startNewGroup(groupName: String)
}