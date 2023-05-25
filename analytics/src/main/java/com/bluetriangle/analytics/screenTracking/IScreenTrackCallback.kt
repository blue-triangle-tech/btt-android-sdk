package com.bluetriangle.analytics.screenTracking

interface IScreenTrackCallback {
    fun onScreenLoad(id: String, className: String, timeTaken: Long)
    fun onScreenView(id: String, className: String, timeTaken: Long)
}