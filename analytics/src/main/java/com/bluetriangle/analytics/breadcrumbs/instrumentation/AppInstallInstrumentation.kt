package com.bluetriangle.analytics.breadcrumbs.instrumentation

import com.bluetriangle.analytics.breadcrumbs.BreadcrumbEvent
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import com.bluetriangle.analytics.eventhub.sdkeventhub.AppLifecycleEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventHub

internal class AppInstallInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) : AppLifecycleEventConsumer,
    BreadcrumbInstrumentation(breadcrumbsCollector) {

    override fun enable() {
        SDKEventHub.instance.addConsumer(this)
    }

    override fun disable() {
        SDKEventHub.instance.removeConsumer(this)
    }

    override fun onAppInstall(appVersion: String) {
        addBreadcrumb(appVersion)
    }

    private fun addBreadcrumb(
        appVersion: String
    ) = collector.get()?.add(
        BreadcrumbEvent.AppInstall(BreadcrumbEvent.AppInstall.AppInstallData(appVersion))
    )
}