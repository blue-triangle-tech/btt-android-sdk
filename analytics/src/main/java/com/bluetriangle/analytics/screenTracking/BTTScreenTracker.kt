package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.model.ScreenType

class BTTScreenTracker(private val pageName: String) {

    private val id = "${pageName}#${System.currentTimeMillis()}"
    var screenType: String = ""
    private var isConsumed = false

    fun onLoadStarted() {
        if(isConsumed) {
            logConsumedError()
            return
        }
        Tracker.instance?.screenTrackMonitor?.onLoadStarted(Screen(
            id,
            pageName,
            ScreenType.Custom(screenType)
        ))
    }

    fun onLoadEnded() {
        if(isConsumed) {
            logConsumedError()
            return
        }
        Tracker.instance?.screenTrackMonitor?.onLoadEnded(Screen(
            id,
            pageName,
            ScreenType.Custom(screenType)
        ))
    }

    fun onViewStarted() {
        if(isConsumed) {
            logConsumedError()
            return
        }
        Tracker.instance?.screenTrackMonitor?.onViewStarted(Screen(
            id,
            pageName,
            ScreenType.Custom(screenType)
        ))
    }

    fun onViewEnded() {
        if(isConsumed) {
            logConsumedError()
            return
        }
        isConsumed = true
        Tracker.instance?.screenTrackMonitor?.onViewEnded(Screen(
            id,
            pageName,
            ScreenType.Custom(screenType)
        ))
    }

    private fun logConsumedError() {
        Tracker.instance?.configuration?.logger?.error("This object is supposed to be used only once for the lifecycle of a view")
    }

}
