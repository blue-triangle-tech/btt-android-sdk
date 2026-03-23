package com.bluetriangle.analytics.breadcrumbs.config

enum class BreadcrumbsFeature(val value: String) {
    AppLifecycle("AppLifecycle"),
    UiLifecycle("UiLifecycle"),
    NetworkRequest("NetworkRequest"),
    NetworkState("NetworkState"),
    AppInstall("AppInstall"),
    AppUpdate("AppUpdate"),
    UserEvent("UserEvent"),
    SystemEvent("SystemEvent")
}