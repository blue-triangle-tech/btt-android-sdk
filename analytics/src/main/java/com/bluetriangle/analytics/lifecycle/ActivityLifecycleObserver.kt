package com.bluetriangle.analytics.lifecycle

import android.app.Activity
import android.os.Bundle

internal interface ActivityLifecycleObserver {
    fun onPreCreated(activity: Activity, savedInstanceState: Bundle?) {}
    fun onCreated(activity: Activity, savedInstanceState: Bundle?) {}
    fun onPostCreated(activity: Activity, savedInstanceState: Bundle?) {}
    fun onPreStarted(activity: Activity) {}
    fun onStarted(activity: Activity) {}
    fun onPostStarted(activity: Activity) {}
    fun onPreResumed(activity: Activity) {}
    fun onResumed(activity: Activity) {}
    fun onPostResumed(activity: Activity) {}
    fun onPrePaused(activity: Activity) {}
    fun onPaused(activity: Activity) {}
    fun onPostPaused(activity: Activity) {}
    fun onPreStopped(activity: Activity) {}
    fun onStopped(activity: Activity) {}
    fun onPostStopped(activity: Activity) {}
    fun onPreSaveInstanceState(activity: Activity, outState: Bundle) {}
    fun onSaveInstanceState(activity: Activity, outState: Bundle) {}
    fun onPostSaveInstanceState(activity: Activity, outState: Bundle) {}
    fun onPreDestroyed(activity: Activity) {}
    fun onDestroyed(activity: Activity) {}
    fun onPostDestroyed(activity: Activity) {}
}