package com.bluetriangle.analytics.anrwatchdog

interface AnrListener {

    fun onAppNotResponding(error:AnrException)

}