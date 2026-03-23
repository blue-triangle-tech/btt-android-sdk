package com.bluetriangle.analytics.breadcrumbs.touchresolver

sealed class TapTarget(val x: Float, val y: Float) {

    val targetClassName: String?
        get() = when(this) {
            is ViewTarget -> className
            is ComposeTarget -> null
        }

    val targetIdentifier: String?
        get() = when(this) {
            is ViewTarget -> resourceId ?: (contentDescription ?: text)
            is ComposeTarget -> trackingName
        }

    class ViewTarget(
        val className: String,
        val resourceId: String?,
        val contentDescription: String?,
        val text: String?,
        x: Float,
        y: Float
    ) : TapTarget(x, y)
    class ComposeTarget(
        val trackingName: String? = null,
        x: Float,
        y: Float
    ) : TapTarget(x, y)
}
