package com.bluetriangle.analytics.screenTracking

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bluetriangle.analytics.utility.registerFragmentLifecycleCallback
import com.bluetriangle.analytics.utility.screen
import com.bluetriangle.analytics.utility.unregisterFragmentLifecycleCallback

internal class ActivityLifecycleTracker(private val screenTracker: ScreenLifecycleTracker, private val fragmentLifecycleTracker: FragmentLifecycleTracker):Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        (activity as? FragmentActivity)?.registerFragmentLifecycleCallback(fragmentLifecycleTracker)
        screenTracker.onLoadStarted(activity.screen)
    }

    override fun onActivityStarted(activity: Activity) {
        screenTracker.onLoadEnded(activity.screen)
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPostResumed(activity: Activity) {
        super.onActivityPostResumed(activity)
        screenTracker.onViewStarted(activity.screen)
    }

    override fun onActivityPaused(activity: Activity) {
        screenTracker.onViewEnded(activity.screen)
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        (activity as? FragmentActivity)?.unregisterFragmentLifecycleCallback(fragmentLifecycleTracker)
    }

}