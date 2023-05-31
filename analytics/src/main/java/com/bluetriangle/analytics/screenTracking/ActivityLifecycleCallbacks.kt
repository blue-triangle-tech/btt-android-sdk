package com.bluetriangle.analytics.screenTracking


import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bluetriangle.analytics.Timer

internal class ActivityLifecycleCallbacks(private val callback: IScreenTrackCallback) :
    Application.ActivityLifecycleCallbacks {
    private val activityLoadMap = mutableMapOf<String, Timer>()
    private val activityViewMap = mutableMapOf<String, Timer>()

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        val className = activity.localClassName
        logToLogcat("Activity Created", className)

        activityLoadMap[className] = Timer("$className - loaded", "AutomaticScreenTrack").start()
        registerFragmentLifecycleCallbacks(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        logToLogcat("Activity Started", activity.localClassName)
    }

    override fun onActivityResumed(activity: Activity) {
        val className = activity.localClassName
        logToLogcat("Activity Resumed", className)

        val timer = activityLoadMap.remove(className)
        if (timer != null) {
            callback.onScreenLoad(
                className,
                className,
                timer
            )
        }

        activityViewMap[className] = Timer("$className - viewed", "AutomaticScreenTrack").start()
    }

    override fun onActivityPaused(activity: Activity) {
        val className = activity.localClassName
        logToLogcat("Activity Paused", className)

        val timer = activityViewMap.remove(className)
        if (timer != null) {
            callback.onScreenView(
                className,
                className,
                timer
            )
        }
    }

    override fun onActivityStopped(activity: Activity) {
        logToLogcat("Activity Stopped", activity.localClassName)
    }

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        logToLogcat("Activity Destroyed", activity.localClassName)
    }

    private fun logToLogcat(tag: String, msg: String) {
        //Log.i(tag, msg)
    }

    private fun registerFragmentLifecycleCallbacks(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                    FragmentLifecycleCallbacks(callback),
                    true
                )
            }
        }
    }
}