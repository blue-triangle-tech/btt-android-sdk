package com.bluetriangle.analytics.screenTracking


import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

internal class ActivityLifecycleCallbacks(private val callback: IScreenTrackCallback) :
    Application.ActivityLifecycleCallbacks {
    private val activityLoadMap = mutableMapOf<String, Long>()
    private val activityViewMap = mutableMapOf<String, Long>()

    override fun onActivityCreated(activity: Activity, p1: Bundle?) {
        logToLogcat("Activity Created", activity.localClassName)
        activityLoadMap[activity.componentName.className] = System.currentTimeMillis()
        registerFragmentLifecycleCallbacks(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        logToLogcat("Activity Started", activity.localClassName)
    }

    override fun onActivityResumed(activity: Activity) {
        logToLogcat("Activity Resumed", activity.localClassName)
        val createTime = activityLoadMap.remove(activity.componentName.className)
        if (createTime != null) {
            callback.onScreenLoad(
                activity.componentName.className,
                activity.localClassName,
                System.currentTimeMillis() - createTime
            )
        }

        activityViewMap[activity.componentName.className] = System.currentTimeMillis()
    }

    override fun onActivityPaused(activity: Activity) {
        logToLogcat("Activity Paused", activity.localClassName)

        val resumeTime = activityViewMap.remove(activity.componentName.className)
        if (resumeTime != null) {
            callback.onScreenView(
                activity.componentName.className,
                activity.localClassName,
                System.currentTimeMillis() - resumeTime
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