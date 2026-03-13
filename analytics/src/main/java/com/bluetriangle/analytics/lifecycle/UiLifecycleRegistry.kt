package com.bluetriangle.analytics.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.GuardedBy
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bluetriangle.analytics.lifecycle.LifecycleRegistry.install


internal object LifecycleRegistry {

    private val lock = Any()

    @GuardedBy("lock") private var application: Application? = null
    @GuardedBy("lock") private var installed = false
    @GuardedBy("lock") private val trackedActivities = mutableSetOf<Activity>()

    private val activityLifecycleDispatcher = ActivityLifecycleDispatcher()
    private val fragmentLifecycleDispatcher = FragmentLifecycleDispatcher()

    private val composeLifecycleDispatcher = ComposeLifecycleDispatcher()

    private val keyboardEventDispatcher = KeyboardEventDispatcher()

    init {
        activityLifecycleDispatcher.addObserver(object : ActivityLifecycleObserver {
            override fun onCreated(activity: Activity, savedInstanceState: Bundle?) {
                super.onCreated(activity, savedInstanceState)
                synchronized(lock) { trackedActivities.add(activity) }
                listenToKeyboardEvents(activity)
                listenToFragments(activity)
            }


            override fun onDestroyed(activity: Activity) {
                super.onDestroyed(activity)
                synchronized(lock) { trackedActivities.remove(activity) }
                stopListeningToKeyboardEvents(activity)
                stopListeningToFragments(activity)
            }
        })
    }

    private fun listenToKeyboardEvents(activity: Activity) {
        ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView, keyboardEventDispatcher)
    }

    private fun listenToFragments(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                fragmentLifecycleDispatcher,
                true
            )
        }
    }

    private fun stopListeningToKeyboardEvents(activity: Activity) {
        ViewCompat.setOnApplyWindowInsetsListener(activity.window.decorView, null)
    }

    private fun stopListeningToFragments(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleDispatcher)
        }
    }

    fun install(application: Application) {
        synchronized(lock) {
            if (installed) return
            this.application = application
            application.registerActivityLifecycleCallbacks(activityLifecycleDispatcher)
            installed = true
        }
    }

    fun uninstall() {
        synchronized(lock) {
            if (!installed) return
            application?.unregisterActivityLifecycleCallbacks(activityLifecycleDispatcher)
            application = null
            trackedActivities.forEach { activity ->
                stopListeningToKeyboardEvents(activity)
                stopListeningToFragments(activity)
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

    fun addKeyboardEventObserver(observer: KeyboardEventObserver) {
        keyboardEventDispatcher.addObserver(observer)
    }

    fun removeKeyboardEventObserver(observer: KeyboardEventObserver) {
        keyboardEventDispatcher.removeObserver(observer)
    }

    fun onEnterComposition(name: String) {
        composeLifecycleDispatcher.onEnterComposition(name)
    }

    fun onLeaveComposition(name: String) {
        composeLifecycleDispatcher.onLeaveComposition(name)
    }
}