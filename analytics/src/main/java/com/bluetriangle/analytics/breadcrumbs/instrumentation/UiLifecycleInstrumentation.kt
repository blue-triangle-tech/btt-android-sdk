package com.bluetriangle.analytics.breadcrumbs.instrumentation

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbEvent
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import com.bluetriangle.analytics.lifecycle.ActivityLifecycleObserver
import com.bluetriangle.analytics.lifecycle.ComposeLifecycleObserver
import com.bluetriangle.analytics.lifecycle.FragmentLifecycleObserver
import com.bluetriangle.analytics.lifecycle.LifecycleRegistry

internal class UiLifecycleInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) : BreadcrumbInstrumentation(
    breadcrumbsCollector
), ActivityLifecycleObserver, FragmentLifecycleObserver {

    private val activityObserver = object : ActivityLifecycleObserver {
        override fun onCreated(activity: Activity, savedInstanceState: Bundle?) {
            super.onCreated(activity, savedInstanceState)
            addBreadcrumb(activity::class.java.simpleName, "onCreated")
        }

        override fun onStarted(activity: Activity) {
            super.onStarted(activity)
            addBreadcrumb(activity::class.java.simpleName, "onStarted")
        }

        override fun onResumed(activity: Activity) {
            super.onResumed(activity)
            addBreadcrumb(activity::class.java.simpleName, "onResumed")
        }

        override fun onPaused(activity: Activity) {
            super.onPaused(activity)
            addBreadcrumb(activity::class.java.simpleName, "onPaused")
        }

        override fun onStopped(activity: Activity) {
            super.onStopped(activity)
            addBreadcrumb(activity::class.java.simpleName, "onStopped")
        }

        override fun onSaveInstanceState(activity: Activity, outState: Bundle) {
            super.onSaveInstanceState(activity, outState)
            addBreadcrumb(activity::class.java.simpleName, "onSaveInstanceState")
        }

        override fun onDestroyed(activity: Activity) {
            super.onDestroyed(activity)
            addBreadcrumb(activity::class.java.simpleName, "onDestroyed")
        }
    }

    private val fragmentObserver = object : FragmentLifecycleObserver {
        override fun onCreated(fragment: Fragment, savedInstanceState: Bundle?) {
            super.onCreated(fragment, savedInstanceState)
            addBreadcrumb(fragment::class.java.simpleName, "onCreate")
        }

        override fun onStarted(fragment: Fragment) {
            super.onStarted(fragment)
            addBreadcrumb(fragment::class.java.simpleName, "onStarted")
        }

        override fun onResumed(fragment: Fragment) {
            super.onResumed(fragment)
            addBreadcrumb(fragment::class.java.simpleName, "onResumed")
        }

        override fun onPaused(fragment: Fragment) {
            super.onPaused(fragment)
            addBreadcrumb(fragment::class.java.simpleName, "onPaused")
        }

        override fun onStopped(fragment: Fragment) {
            super.onStopped(fragment)
            addBreadcrumb(fragment::class.java.simpleName, "onStopped")
        }

        override fun onSaveInstanceState(fragment: Fragment, outState: Bundle) {
            super.onSaveInstanceState(fragment, outState)
            addBreadcrumb(fragment::class.java.simpleName, "onSaveInstanceState")
        }

        override fun onDestroyed(fragment: Fragment) {
            super.onDestroyed(fragment)
            addBreadcrumb(fragment::class.java.simpleName, "onDestroyed")
        }
    }

    private val composeLifecycleObserver = object: ComposeLifecycleObserver {
        override fun onEnterComposition(name: String) {
            addBreadcrumb(name, "enterComposition")
        }

        override fun onLeaveComposition(name: String) {
            addBreadcrumb(name, "leaveComposition")
        }
    }

    override fun enable() {
        LifecycleRegistry.addActivityObserver(activityObserver)
        LifecycleRegistry.addFragmentObserver(fragmentObserver)
        LifecycleRegistry.addComposeObserver(composeLifecycleObserver)
    }

    override fun disable() {
        LifecycleRegistry.removeActivityObserver(activityObserver)
        LifecycleRegistry.removeFragmentObserver(fragmentObserver)
        LifecycleRegistry.removeComposeObserver(composeLifecycleObserver)
    }

    private fun addBreadcrumb(
        className: String,
        event: String
    ) = collector.get()?.add(
        BreadcrumbEvent.UiLifecycle(BreadcrumbEvent.UiLifecycle.UiLifecycleData(className, event))
    )


}