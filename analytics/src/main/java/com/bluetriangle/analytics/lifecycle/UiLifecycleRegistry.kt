package com.bluetriangle.analytics.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.GuardedBy
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bluetriangle.analytics.lifecycle.LifecycleRegistry.install

/**
 * Central lifecycle registry for the SDK.
 *
 * Hooks into [Application.ActivityLifecycleCallbacks] to track all Activities,
 * and automatically registers a [FragmentManager.FragmentLifecycleCallbacks] on
 * every [FragmentActivity] so Fragment events are tracked across the whole app.
 *
 * // Any feature registers itself:
 * ```
 * LifecycleRegistry.addActivityObserver(myFeature)
 * LifecycleRegistry.addFragmentObserver(myFeature)
 * ```
 */
internal object LifecycleRegistry {

    private val lock = Any()

    @GuardedBy("lock") private var application: Application? = null
    @GuardedBy("lock") private var installed = false
    @GuardedBy("lock") private val trackedActivities = mutableSetOf<FragmentActivity>()

    private val activityLifecycleDispatcher = ActivityLifecycleDispatcher()
    private val fragmentLifecycleDispatcher = FragmentLifecycleDispatcher()

    private val composeLifecycleDispatcher = ComposeLifecycleDispatcher()

    init {
        activityLifecycleDispatcher.addObserver(object : ActivityLifecycleObserver {
            override fun onCreated(activity: Activity, savedInstanceState: Bundle?) {
                super.onCreated(activity, savedInstanceState)
                if (activity is FragmentActivity) {
                    synchronized(lock) { trackedActivities.add(activity) }
                    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                        fragmentLifecycleDispatcher,
                        true
                    )
                }
            }

            override fun onDestroyed(activity: Activity) {
                super.onDestroyed(activity)
                if (activity is FragmentActivity) {
                    activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleDispatcher)
                    synchronized(lock) { trackedActivities.remove(activity) }
                }
            }
        })
    }

    /**
     * Installs the registry against the given [Application].
     * Safe to call multiple times — subsequent calls are no-ops.
     */
    fun install(application: Application) {
        synchronized(lock) {
            if (installed) return
            this.application = application
            application.registerActivityLifecycleCallbacks(activityLifecycleDispatcher)
            installed = true
        }
    }

    /**
     * Uninstalls the registry and clears all observers.
     * After calling this, [install] must be called again to resume tracking.
     */
    fun uninstall() {
        synchronized(lock) {
            if (!installed) return
            application?.unregisterActivityLifecycleCallbacks(activityLifecycleDispatcher)
            application = null
            // Unregister fragment callbacks from any activities still alive at this point.
            trackedActivities.forEach { activity ->
                activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleDispatcher)
            }
            trackedActivities.clear()
            activityLifecycleDispatcher.clear()
            fragmentLifecycleDispatcher.clear()
            installed = false
        }
    }

    fun addActivityObserver(observer: ActivityLifecycleObserver) {
        activityLifecycleDispatcher.addObserver(observer)
    }

    fun removeActivityObserver(observer: ActivityLifecycleObserver) {
        activityLifecycleDispatcher.removeObserver(observer)
    }

    fun addFragmentObserver(observer: FragmentLifecycleObserver) {
        fragmentLifecycleDispatcher.addObserver(observer)
    }

    fun removeFragmentObserver(observer: FragmentLifecycleObserver) {
        fragmentLifecycleDispatcher.removeObserver(observer)
    }

    fun addComposeObserver(observer: ComposeLifecycleObserver) {
        composeLifecycleDispatcher.addObserver(observer)
    }

    fun removeComposeObserver(observer: ComposeLifecycleObserver) {
        composeLifecycleDispatcher.removeObserver(observer)
    }

    fun onEnterComposition(name: String) {
        composeLifecycleDispatcher.onEnterComposition(name)
    }

    fun onLeaveComposition(name: String) {
        composeLifecycleDispatcher.onLeaveComposition(name)
    }
}