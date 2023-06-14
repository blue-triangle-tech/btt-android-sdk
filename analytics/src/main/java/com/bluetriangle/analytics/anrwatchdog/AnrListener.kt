package com.bluetriangle.analytics.anrwatchdog

internal interface AnrListener {

    fun onAppNotResponding(error: AnrException)

}