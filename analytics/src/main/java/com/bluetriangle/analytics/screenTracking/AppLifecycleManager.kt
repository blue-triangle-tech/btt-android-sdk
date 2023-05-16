package com.bluetriangle.analytics.screenTracking

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class AppLifecycleManager : Application.ActivityLifecycleCallbacks {
    private val startedActivities: MutableList<Activity?> = ArrayList()
    private var visibleActivity: Activity? = null

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (visibleActivity == null || visibleActivity!!.taskId != activity.taskId) {
            visibleActivity = activity
            Log.i("Screen Tracking", activity.localClassName)
            //TODO:: report screen view event
        }
    }

    override fun onActivityStarted(activity: Activity) {
        startedActivities.add(0, activity)
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivities.remove(activity)
    }
}