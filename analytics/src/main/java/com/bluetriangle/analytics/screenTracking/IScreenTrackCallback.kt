package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Timer

internal interface IScreenTrackCallback {
    fun onScreenLoad(id: String, className: String, timer: Timer)
    fun onScreenView(id: String, className: String, timer: Timer)
}