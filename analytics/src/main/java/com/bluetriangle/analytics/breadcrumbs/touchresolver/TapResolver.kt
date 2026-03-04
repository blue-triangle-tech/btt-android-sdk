package com.bluetriangle.analytics.breadcrumbs.touchresolver

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ComposeView

sealed class TapTarget {

    val targetClassName: String?
        get() = when(this) {
            is ViewTargetInfo -> className
            is ComposeTargetInfo -> null
        }

    val targetIdentifier: String?
        get() = when(this) {
            is ViewTargetInfo -> resourceId ?: contentDescription
            is ComposeTargetInfo -> trackingName
        }

    class ViewTargetInfo(
        val className: String,
        val resourceId: String?,
        val contentDescription: String?,
        val text: String?
    ) : TapTarget()
    class ComposeTargetInfo(
        val trackingName: String
    ) : TapTarget()
}
