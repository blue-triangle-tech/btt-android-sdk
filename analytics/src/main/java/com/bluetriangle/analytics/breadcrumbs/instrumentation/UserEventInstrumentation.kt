package com.bluetriangle.analytics.breadcrumbs.instrumentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbEvent
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import com.bluetriangle.analytics.breadcrumbs.InteractionWindowCallback
import com.bluetriangle.analytics.lifecycle.ActivityLifecycleObserver
import com.bluetriangle.analytics.lifecycle.FragmentLifecycleObserver
import com.bluetriangle.analytics.lifecycle.LifecycleRegistry

internal class UserEventInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) : BreadcrumbInstrumentation(
    breadcrumbsCollector
), ActivityLifecycleObserver {

    private val activityObserver = object : ActivityLifecycleObserver {
        override fun onStarted(activity: Activity) {
            activity.window.callback = InteractionWindowCallback(
                activity,
                activity.window.callback
            ) { eventType, userEvent ->
                userEvent?.let {
                    addBreadcrumb(eventType.value,
                        userEvent.targetClassName,
                        userEvent.targetIdentifier,
                        userEvent.x,
                        userEvent.y
                    )
                }
            }
        }

        override fun onStopped(activity: Activity) {
            (activity.window.callback as? InteractionWindowCallback)?.let {
                activity.window.callback = it.wrapped
            }
        }
    }

    override fun enable() {
        LifecycleRegistry.addActivityObserver(activityObserver)
    }

    override fun disable() {
        LifecycleRegistry.removeActivityObserver(activityObserver)
    }

    private fun addBreadcrumb(
        action: String,
        targetClass: String?,
        targetId: String?,
        x: Float,
        y: Float
    ) = collector.get()?.add(
        BreadcrumbEvent.UserEvent(BreadcrumbEvent.UserEvent.UserEventData(action, targetClass, targetId, x, y))
    )


}