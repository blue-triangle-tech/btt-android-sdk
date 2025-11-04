package com.bluetriangle.analytics.screenTracking

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.utility.registerFragmentLifecycleCallback
import com.bluetriangle.analytics.utility.screen
import com.bluetriangle.analytics.utility.unregisterFragmentLifecycleCallback

internal class ActivityLifecycleTracker(private val screenTracker: ScreenLifecycleTracker, private val fragmentLifecycleTracker: FragmentLifecycleTracker):Application.ActivityLifecycleCallbacks {

    companion object {
        const val TAG = "ActivityLifecycleTracker"
    }

    private var activities = arrayListOf<Activity>()

    @Synchronized
    fun unregister() {
        activities.forEach {
            (it as? FragmentActivity)?.unregisterFragmentLifecycleCallback(fragmentLifecycleTracker)
        }
    }

    @Synchronized
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logEvent("onActivityCreated", activity)
        activities.add(activity)
        (activity as? FragmentActivity)?.registerFragmentLifecycleCallback(fragmentLifecycleTracker)
        val originalCallback = activity.window.callback
        activity.window.callback = TouchEventInterceptor(originalCallback)
        screenTracker.onLoadStarted(activity.screen, automated = true)
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

    @Synchronized
    override fun onActivityDestroyed(activity: Activity) {
        logEvent("onActivityDestroyed", activity)
        activities.remove(activity)
        (activity as? FragmentActivity)?.unregisterFragmentLifecycleCallback(fragmentLifecycleTracker)
    }

    fun logEvent(event: String, activity: Any) {
        Tracker.instance?.configuration?.logger?.info("${TAG}, $event: ${activity::class.java.simpleName}")
    }
}