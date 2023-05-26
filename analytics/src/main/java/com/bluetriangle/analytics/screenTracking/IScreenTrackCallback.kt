package com.bluetriangle.analytics.screenTracking

interface IScreenTrackCallback {
    fun onScreenLoad(id: String, className: String, startTime: Long)
    fun onScreenView(id: String, className: String, startTime: Long)
}