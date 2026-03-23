package com.bluetriangle.analytics.eventhub

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import android.os.Bundle

internal interface AppEventConsumer {

    fun onAppCreated(application: Application, timestamp: Long) {

    }

    fun onActivityCreated(activity: Activity, data: Bundle?) {

    }

    fun onActivityStarted(activity: Activity) {

    }

    fun onActivityResumed(activity: Activity) {

    }

    fun onAppMovedToBackground(application:Application) {

    }

    fun onLowMemory() {

    }

    fun onTrimMemory(level: String) {

    }

    fun onConfigurationChanged(configuration: Configuration) {

    }

}