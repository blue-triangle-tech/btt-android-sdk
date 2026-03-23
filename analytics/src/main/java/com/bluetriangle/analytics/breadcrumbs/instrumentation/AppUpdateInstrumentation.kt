package com.bluetriangle.analytics.breadcrumbs.instrumentation

import com.bluetriangle.analytics.breadcrumbs.BreadcrumbEvent
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import com.bluetriangle.analytics.eventhub.sdkeventhub.AppLifecycleEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventHub

internal class AppUpdateInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) : AppLifecycleEventConsumer,
    BreadcrumbInstrumentation(breadcrumbsCollector) {

    override fun enable() {
        SDKEventHub.instance.addConsumer(this)
    }

    override fun disable() {
        SDKEventHub.instance.removeConsumer(this)
    }

    override fun onAppUpdate(oldVersion: String, newVersion: String) {
        addBreadcrumb(oldVersion, newVersion)
    }

    private fun addBreadcrumb(
        oldVersion: String,
        newVersion: String
    ) = collector.get()?.add(
        BreadcrumbEvent.AppUpdate(BreadcrumbEvent.AppUpdate.AppUpdateData(oldVersion, newVersion))
    )
}