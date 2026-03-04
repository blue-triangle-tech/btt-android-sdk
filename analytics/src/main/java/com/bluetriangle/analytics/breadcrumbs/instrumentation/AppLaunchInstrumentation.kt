package com.bluetriangle.analytics.breadcrumbs.instrumentation

import com.bluetriangle.analytics.breadcrumbs.BreadcrumbEvent
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import com.bluetriangle.analytics.eventhub.sdkeventhub.AppLifecycleEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventHub
import com.bluetriangle.analytics.launchtime.model.LaunchType

internal class AppLaunchInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) : AppLifecycleEventConsumer,
    BreadcrumbInstrumentation(breadcrumbsCollector) {

    override fun enable() {
        SDKEventHub.instance.addConsumer(this)
    }

    override fun disable() {
        SDKEventHub.instance.removeConsumer(this)
    }

    override fun onLaunchDetected(launchType: LaunchType) {
        addBreadcrumb(launchType.name.lowercase())
    }

    private fun addBreadcrumb(
        launchType: String
    ) = collector.get()?.add(
        BreadcrumbEvent.AppLaunch(BreadcrumbEvent.AppLaunch.AppLaunchData(launchType))
    )
}