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

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        registerFragmentLifecycleCallbacks(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (visibleActivity == null || visibleActivity!!.localClassName != activity.localClassName) {
            visibleActivity = activity
            Log.i("Act Screen Tracking", activity.localClassName)
            //TODO:: report screen view event
        }
    }

    override fun onActivityStarted(activity: Activity) {
        startedActivities.add(0, activity)
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivities.remove(activity)
    }

    private fun registerFragmentLifecycleCallbacks(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                    FragmentLifecycleCallbacks(),
                    false
                )
            }
        }
    }
}