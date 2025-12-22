package com.bluetriangle.analytics.screenTracking

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.utility.registerFragmentLifecycleCallback
import com.bluetriangle.analytics.utility.screen
import com.bluetriangle.analytics.utility.unregisterFragmentLifecycleCallback

internal class ActivityLifecycleTracker(
    private val configuration: BlueTriangleConfiguration,
    private val screenTracker: ScreenLifecycleTracker,
    private val fragmentLifecycleTracker: FragmentLifecycleTracker
):Application.ActivityLifecycleCallbacks {

    companion object {
        const val TAG = "ActivityLifecycleTracker"
    }

    private var activities = arrayListOf<Activity>()

    fun unregister() {
        synchronized(activities) {
            activities.forEach {
                it.disableTapDetection()
                (it as? FragmentActivity)?.unregisterFragmentLifecycleCallback(fragmentLifecycleTracker)
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logEvent("onActivityCreated", activity)
        synchronized(activities) {
            activities.add(activity)
        }
        (activity as? FragmentActivity)?.registerFragmentLifecycleCallback(fragmentLifecycleTracker)
        if(configuration.shouldDetectTap) {
            activity.enableTapDetection()
        }
        screenTracker.onLoadStarted(activity.screen, automated = true)
    }

    fun enableTapDetection() {
        synchronized(activities) {
            activities.forEach {
                it.enableTapDetection()
            }
        }
    }

    fun disableTapDetection() {
        synchronized(activities) {
            activities.forEach {
                it.disableTapDetection()
            }
        }
    }

    private fun Activity.enableTapDetection() {
        val originalCallback = window.callback
        window.callback = TouchEventInterceptor(originalCallback)
    }

    private fun Activity.disableTapDetection() {
        Tracker.instance?.lastTouchEventTimestamp = 0L
        (window.callback as? TouchEventInterceptor)?.originalCallback?.let {
            window.callback = it
        }
    }

    override fun onActivityStarted(activity: Activity) {
        logEvent("onActivityStarted", activity)
        screenTracker.onLoadEnded(activity.screen, automated = true)
    }

    override fun onActivityResumed(activity: Activity) {
        logEvent("onActivityResumed", activity)
    }

    override fun onActivityPostResumed(activity: Activity) {
        logEvent("onActivityPostResumed", activity)
        super.onActivityPostResumed(activity)
        val screen = activity.screen
        screen.fetchTitle(activity)
        screenTracker.onViewStarted(screen, automated = true)
    }

    override fun onActivityPaused(activity: Activity) {
        logEvent("onActivityPaused", activity)
        screenTracker.onViewEnded(activity.screen, automated = true)
    }

    override fun onActivityStopped(activity: Activity) {
        logEvent("onActivityStopped", activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logEvent("onActivitySaveInstanceState", activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        logEvent("onActivityDestroyed", activity)
        synchronized(activities) {
            activities.remove(activity)
        }
        activity.disableTapDetection()
        (activity as? FragmentActivity)?.unregisterFragmentLifecycleCallback(fragmentLifecycleTracker)
    }

    fun logEvent(event: String, activity: Any) {
        Tracker.instance?.configuration?.logger?.info("${TAG}, $event: ${activity::class.java.simpleName}")
    }
}