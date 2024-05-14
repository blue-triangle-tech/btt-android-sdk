package com.bluetriangle.analytics.launchtime.helpers

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.bluetriangle.analytics.launchtime.AppEventConsumer

internal class ActivityEventHandler(val listener: AppEventConsumer) :
    Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, data: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        listener.onActivityStarted(activity)
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPostResumed(activity: Activity) {
        listener.onActivityResumed(activity)
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, data: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

}
