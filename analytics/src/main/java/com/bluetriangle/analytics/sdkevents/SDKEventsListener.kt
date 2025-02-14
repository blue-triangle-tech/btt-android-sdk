package com.bluetriangle.analytics.sdkevents

import android.app.Application

interface SDKEventsListener {

    fun onConfigured(application: Application, configuration: SDKConfiguration)

    fun onEnabled(application: Application, configuration: SDKConfiguration)

    fun onDisabled(application: Application, configuration: SDKConfiguration)

    fun onSessionChanged(application: Application, configuration: SDKConfiguration)

}