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
    private var instrumentations: MutableMap<BreadcrumbsFeature, BreadcrumbInstrumentation> = mutableMapOf()

    private var features = BreadcrumbsFeature.values().filterNot { config.ignoredFeatures.contains(it) }

    fun install() {
        breadcrumbsCollector = BreadcrumbsCollector(config.capacity)

        breadcrumbsCollector?.let { collector ->
            instrumentations = features.associateWith {
                featureToInstrumentation(it, collector)
            }.toMutableMap()
        }

        instrumentations.values.forEach {
            it.enable()
        }
    }

    fun uninstall() {
        instrumentations.values.forEach {
            it.disable()
        }
        instrumentations = mutableMapOf()
        breadcrumbsCollector?.clear()
        breadcrumbsCollector = null
    }

    fun featureToInstrumentation(feature: BreadcrumbsFeature, collector: BreadcrumbsCollector) = when(feature) {
        BreadcrumbsFeature.AppLifecycle -> AppLifecycleInstrumentation(collector)
        BreadcrumbsFeature.UiLifecycle -> UiLifecycleInstrumentation(collector)
        BreadcrumbsFeature.NetworkRequest -> NetworkRequestInstrumentation(collector)
        BreadcrumbsFeature.NetworkState -> NetworkStateInstrumentation(collector)
        BreadcrumbsFeature.AppInstall -> AppInstallInstrumentation(collector)
        BreadcrumbsFeature.AppUpdate -> AppUpdateInstrumentation(collector)
        BreadcrumbsFeature.UserEvent -> UserEventInstrumentation(collector)
        BreadcrumbsFeature.SystemEvent -> SystemEventsInstrumentation(collector)
    }

    fun updateConfig(config: BreadcrumbsConfig) {
        val collector = breadcrumbsCollector?: return
        val newFeatures = BreadcrumbsFeature.values().filterNot { config.ignoredFeatures.contains(it) }
        val toBeDisabled = features.filterNot { newFeatures.contains(it) }
        val toBeEnabled = newFeatures.filterNot { features.contains(it) }

        toBeDisabled.forEach {
            instrumentations[it]?.disable()
            instrumentations.remove(it)
        }
        toBeEnabled.forEach {
            instrumentations[it] = featureToInstrumentation(it, collector)
            instrumentations[it]?.enable()
        }
    }

    fun snapshot(): JSONArray? {
        return breadcrumbsCollector?.snapshot()
    }

}