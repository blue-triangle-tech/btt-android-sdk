package com.bluetriangle.analytics.launchtime.helpers

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.bluetriangle.analytics.AppEventHub

internal class ActivityEventHandler :
    Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, data: Bundle?) {
        AppEventHub.instance.onActivityCreated(activity, data)
    }

    override fun onActivityStarted(activity: Activity) {
        AppEventHub.instance.onActivityStarted(activity)
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPostResumed(activity: Activity) {
        AppEventHub.instance.onActivityResumed(activity)
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
