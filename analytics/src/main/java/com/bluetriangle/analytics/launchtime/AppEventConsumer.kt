package com.bluetriangle.analytics.launchtime

import android.app.Activity
import android.app.Application

internal interface AppEventConsumer {

    fun onAppCreated(application: Application)

    fun onActivityStarted(activity: Activity)

    fun onActivityResumed(activity: Activity)

    fun onAppMovedToBackground(application:Application)

}