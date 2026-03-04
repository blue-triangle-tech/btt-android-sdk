package com.bluetriangle.analytics.breadcrumbs.touchresolver

import androidx.compose.ui.platform.AbstractComposeView


internal fun resolveInsideComposeView(
    @Suppress("UNUSED_PARAMETER") composeView: AbstractComposeView,
    rawX: Float,
    rawY: Float,
): TapTarget.ComposeTargetInfo? {
    val node = BttComposeRegistry.hitTest(rawX, rawY) ?: return null
    return TapTarget.ComposeTargetInfo(
        trackingName = node.name
    )
}
