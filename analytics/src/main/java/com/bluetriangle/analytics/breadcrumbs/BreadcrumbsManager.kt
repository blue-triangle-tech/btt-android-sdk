package com.bluetriangle.analytics.breadcrumbs

import com.bluetriangle.analytics.breadcrumbs.config.BreadcrumbsConfig
import com.bluetriangle.analytics.breadcrumbs.config.BreadcrumbsFeature
import com.bluetriangle.analytics.breadcrumbs.instrumentation.AppInstallInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.AppLifecycleInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.AppUpdateInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.BreadcrumbInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.NetworkRequestInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.NetworkStateInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.SystemEventsInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.UiLifecycleInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.UserEventInstrumentation
import org.json.JSONArray

internal class BreadcrumbsManager(var config: BreadcrumbsConfig) {
    private var breadcrumbsCollector: BreadcrumbsCollector? = null
    private var instrumentations: List<BreadcrumbInstrumentation> = emptyList()

    fun install() {
        breadcrumbsCollector = BreadcrumbsCollector(config.capacity)

        breadcrumbsCollector?.let { collector ->
            instrumentations = BreadcrumbsFeature.values().filterNot { config.ignoredFeatures.contains(it) }.map {
                when(it) {
                    BreadcrumbsFeature.AppLifecycle -> AppLifecycleInstrumentation(collector)
                    BreadcrumbsFeature.UiLifecycle -> UiLifecycleInstrumentation(collector)
                    BreadcrumbsFeature.NetworkRequest -> NetworkRequestInstrumentation(collector)
                    BreadcrumbsFeature.NetworkState -> NetworkStateInstrumentation(collector)
                    BreadcrumbsFeature.AppInstall -> AppInstallInstrumentation(collector)
                    BreadcrumbsFeature.AppUpdate -> AppUpdateInstrumentation(collector)
                    BreadcrumbsFeature.UserEvent -> UserEventInstrumentation(collector)
                    BreadcrumbsFeature.SystemEvent -> SystemEventsInstrumentation(collector)
                }
            }
        }

        instrumentations.forEach {
            it.enable()
        }
    }

    fun uninstall() {
        instrumentations.forEach {
            it.disable()
        }
        instrumentations = emptyList()
        breadcrumbsCollector?.clear()
        breadcrumbsCollector = null
    }

    fun snapshot(): JSONArray? {
        return breadcrumbsCollector?.snapshot()
    }

}