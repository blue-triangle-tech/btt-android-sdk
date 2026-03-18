package com.bluetriangle.analytics.breadcrumbs.instrumentation

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbEvent
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import com.bluetriangle.analytics.eventhub.AppEventConsumer
import com.bluetriangle.analytics.eventhub.AppEventHub
import com.bluetriangle.analytics.lifecycle.KeyboardEventObserver
import com.bluetriangle.analytics.lifecycle.LifecycleRegistry
import com.bluetriangle.analytics.utility.orientationToString

internal class SystemEventsInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) : BreadcrumbInstrumentation(
    breadcrumbsCollector
), AppEventConsumer {
    private var currentConfiguration: Configuration? = null

    override fun onConfigurationChanged(configuration: Configuration) {
        val current = currentConfiguration
        if(current == null) {
            addBreadcrumb(
                "orientation",
                configuration.orientationToString()
            )
        } else {
            checkDiffAndAddBreadcrumbs(current, configuration)
        }
        currentConfiguration = Configuration(configuration)
    }

    private fun checkDiffAndAddBreadcrumbs(
        current: Configuration,
        updated: Configuration
    ) {
        val diff = current.diff(updated)
        if ((diff and ActivityInfo.CONFIG_ORIENTATION) != 0) {
            addBreadcrumb(
                "orientation",
                updated.orientationToString()
            )
        }
    }

    private val keyboardLifecycleObserver = object : KeyboardEventObserver {
        override fun onKeyboardShown() {
            addBreadcrumb("keyboard", "shown")
        }

        override fun onKeyboardHidden() {
            addBreadcrumb("keyboard", "hidden")
        }
    }

    override fun enable() {
        AppEventHub.instance.addConsumer(this)
        LifecycleRegistry.addKeyboardEventObserver(keyboardLifecycleObserver)
    }

    override fun disable() {
        AppEventHub.instance.removeConsumer(this)
        LifecycleRegistry.removeKeyboardEventObserver(keyboardLifecycleObserver)
    }

    private fun addBreadcrumb(
        type: String,
        event: String
    ) = collector.get()?.add(
        BreadcrumbEvent.SystemEvent(BreadcrumbEvent.SystemEvent.SystemEventData(type, event))
    )

}