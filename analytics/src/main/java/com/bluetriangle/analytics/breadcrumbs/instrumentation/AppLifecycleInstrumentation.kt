package com.bluetriangle.analytics.breadcrumbs.instrumentation

import android.app.Activity
import android.app.Application
import android.content.res.Configuration
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbEvent
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import com.bluetriangle.analytics.eventhub.AppEventConsumer
import com.bluetriangle.analytics.eventhub.AppEventHub
import com.bluetriangle.analytics.utility.getConfigurationChanges

internal class AppLifecycleInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) : AppEventConsumer,
    BreadcrumbInstrumentation(breadcrumbsCollector) {

    private var currentConfiguration: Configuration? = null

    override fun onAppCreated(application: Application) {
        addBreadcrumb("Application.onCreate")
    }

    override fun onLowMemory() {
        addBreadcrumb("Application.onLowMemory")
    }

    override fun onTrimMemory(level: String) {
        addBreadcrumb("Application.onTrimMemory(${level})")
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        currentConfiguration?.let {
            getConfigurationChanges(it, configuration).forEach { change ->
                addBreadcrumb(change.toString())
            }
        }
        currentConfiguration = Configuration(configuration)
    }

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        addBreadcrumb("foreground")
    }

    override fun onAppMovedToBackground(application: Application) {
        super.onAppMovedToBackground(application)
        addBreadcrumb("background")
    }

    override fun enable() {
        AppEventHub.instance.addConsumer(this)
    }

    override fun disable() {
        AppEventHub.instance.removeConsumer(this)
    }

    private fun addBreadcrumb(
        event: String
    ) = collector.get()?.add(
        BreadcrumbEvent.AppLifecycle(BreadcrumbEvent.AppLifecycle.AppLifecycleData(event))
    )
}