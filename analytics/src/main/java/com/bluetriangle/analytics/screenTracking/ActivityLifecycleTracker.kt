package com.bluetriangle.analytics.screenTracking

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bluetriangle.analytics.utility.logD
import com.bluetriangle.analytics.utility.registerFragmentLifecycleCallback
import com.bluetriangle.analytics.utility.screen
import com.bluetriangle.analytics.utility.unregisterFragmentLifecycleCallback

internal class ActivityLifecycleTracker(private val screenTracker: ScreenLifecycleTracker, private val fragmentLifecycleTracker: FragmentLifecycleTracker):Application.ActivityLifecycleCallbacks {

    val TAG: String = this::class.java.simpleName

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logD(TAG, "onActivityCreated: ${activity.screen}")
        (activity as? FragmentActivity)?.registerFragmentLifecycleCallback(fragmentLifecycleTracker)
        screenTracker.onLoadStarted(activity.screen)
    }

    override fun onActivityStarted(activity: Activity) {
        logD(TAG, "onActivityStarted: ${activity.screen}")
        screenTracker.onLoadEnded(activity.screen)
    }

    override fun onActivityResumed(activity: Activity) {
        logD(TAG, "onActivityResumed: ${activity.screen}")
    }

    override fun onActivityPostResumed(activity: Activity) {
        super.onActivityPostResumed(activity)
        logD(TAG, "onActivityPostResumed: ${activity.screen}")
        screenTracker.onViewStarted(activity.screen)
    }

    override fun onActivityPaused(activity: Activity) {
        logD(TAG, "onActivityPaused: ${activity.screen}")
        screenTracker.onViewEnded(activity.screen)
    }

    override fun onActivityStopped(activity: Activity) {
        logD(TAG, "onActivityStopped: ${activity.screen}")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logD(TAG, "onActivitySaveInstanceState: ${activity.screen}")
    }

    override fun onActivityDestroyed(activity: Activity) {
        logD(TAG, "onActivityDestroyed: ${activity.screen}")
        (activity as? FragmentActivity)?.unregisterFragmentLifecycleCallback(fragmentLifecycleTracker)
    }

}