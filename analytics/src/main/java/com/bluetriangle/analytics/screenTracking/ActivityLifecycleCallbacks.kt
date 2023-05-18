package com.bluetriangle.analytics.screenTracking


import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity

class ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    private val startedActivities: MutableList<Activity?> = ArrayList()
    private var visibleActivity: Activity? = null

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        Log.i("Activity Created", activity.localClassName)
        registerFragmentLifecycleCallbacks(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        Log.i("Activity Started", activity.localClassName)
        startedActivities.remove(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        if (visibleActivity == null || visibleActivity!!.localClassName != activity.localClassName) {
            visibleActivity = activity
            Log.i("Activity Resumed", activity.localClassName)
        }
    }

    override fun onActivityPaused(activity: Activity) {
        Log.i("Activity Paused", activity.localClassName)
    }

    override fun onActivityStopped(activity: Activity) {
        Log.i("Activity Stopped", activity.localClassName)
        startedActivities.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.i("Activity Destroyed", activity.localClassName)
    }

    private fun registerFragmentLifecycleCallbacks(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                    FragmentLifecycleCallbacks(),
                    true
                )
            }
        }
    }
}