package com.bluetriangle.analytics.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.concurrent.CopyOnWriteArraySet

internal class ActivityLifecycleDispatcher : Application.ActivityLifecycleCallbacks {
    private val observers = CopyOnWriteArraySet<ActivityLifecycleObserver>()

    fun addObserver(observer: ActivityLifecycleObserver) {
        observers += observer
    }

    fun removeObserver(observer: ActivityLifecycleObserver) {
        observers -= observer
    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        observers.forEach { it.onPreCreated(activity, savedInstanceState) }
    }

    override fun onActivityCreated(var1: Activity, var2: Bundle?) {
        observers.forEach { it.onCreated(var1, var2) }
    }

    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
        observers.forEach { it.onPostCreated(activity, savedInstanceState) }
    }

    override fun onActivityPreStarted(activity: Activity) {
        observers.forEach { it.onPreStarted(activity) }
    }

    override fun onActivityStarted(var1: Activity) {
        observers.forEach { it.onStarted(var1) }
    }

    override fun onActivityPostStarted(activity: Activity) {
        observers.forEach { it.onPostStarted(activity) }
    }

    override fun onActivityPreResumed(activity: Activity) {
        observers.forEach { it.onPreResumed(activity) }
    }

    override fun onActivityResumed(var1: Activity) {
        observers.forEach { it.onResumed(var1) }
    }

    override fun onActivityPostResumed(activity: Activity) {
        observers.forEach { it.onPostResumed(activity) }
    }

    override fun onActivityPrePaused(activity: Activity) {
        observers.forEach { it.onPrePaused(activity) }
    }

    override fun onActivityPaused(var1: Activity) {
        observers.forEach { it.onPaused(var1) }
    }

    override fun onActivityPostPaused(activity: Activity) {
        observers.forEach { it.onPostPaused(activity) }
    }

    override fun onActivityPreStopped(activity: Activity) {
        observers.forEach { it.onPreStopped(activity) }
    }

    override fun onActivityStopped(var1: Activity) {
        observers.forEach { it.onStopped(var1) }
    }

    override fun onActivityPostStopped(activity: Activity) {
        observers.forEach { it.onPostStopped(activity) }
    }

    override fun onActivityPreSaveInstanceState(activity: Activity, outState: Bundle) {
        observers.forEach { it.onPreSaveInstanceState(activity, outState) }
    }

    override fun onActivitySaveInstanceState(var1: Activity, var2: Bundle) {
        observers.forEach { it.onSaveInstanceState(var1, var2) }
    }

    override fun onActivityPostSaveInstanceState(activity: Activity, outState: Bundle) {
        observers.forEach { it.onPostSaveInstanceState(activity, outState) }
    }

    override fun onActivityPreDestroyed(activity: Activity) {
        observers.forEach { it.onPreDestroyed(activity) }
    }

    override fun onActivityDestroyed(var1: Activity) {
        observers.forEach { it.onDestroyed(var1) }
    }

    override fun onActivityPostDestroyed(activity: Activity) {
        observers.forEach { it.onPostDestroyed(activity) }
    }

    fun clear() {
        observers.clear()
    }

}