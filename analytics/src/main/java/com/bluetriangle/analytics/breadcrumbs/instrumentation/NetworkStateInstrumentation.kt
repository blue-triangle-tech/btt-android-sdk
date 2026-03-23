package com.bluetriangle.analytics.breadcrumbs.instrumentation

import com.bluetriangle.analytics.breadcrumbs.BreadcrumbEvent
import com.bluetriangle.analytics.breadcrumbs.BreadcrumbsCollector
import com.bluetriangle.analytics.eventhub.sdkeventhub.NetworkEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventConsumer
import com.bluetriangle.analytics.eventhub.sdkeventhub.SDKEventHub
import com.bluetriangle.analytics.networkstate.BTTNetworkState
import com.bluetriangle.analytics.utility.value

internal class NetworkStateInstrumentation(breadcrumbsCollector: BreadcrumbsCollector) : NetworkEventConsumer,
    BreadcrumbInstrumentation(breadcrumbsCollector) {

    override fun enable() {
        SDKEventHub.instance.addConsumer(this)
    }

    override fun disable() {
        SDKEventHub.instance.removeConsumer(this)
    }

    override fun onNetworkStateChanged(networkState: BTTNetworkState) {
        addBreadcrumb(networkState.value)
    }

    private fun addBreadcrumb(
        state: String
    ) = collector.get()?.add(
        BreadcrumbEvent.NetworkState(BreadcrumbEvent.NetworkState.NetworkStateData(state))
    )
}