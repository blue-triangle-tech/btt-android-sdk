package com.bluetriangle.analytics.breadcrumbs.touchresolver

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ComposeView

internal data class ViewHitResult(
    val target: TapTarget
)

internal fun findViewAtPoint(
    root: View,
    rawX: Float,
    rawY: Float,
): ViewHitResult? {
    if (!root.isVisible()) return null
    if (!root.containsScreenPoint(rawX, rawY)) return null

    if (root is ViewGroup) {
        if(root::class.simpleName == "TabView") {
            return ViewHitResult(
                root.toTargetInfo()
            )
        }
        for (i in root.childCount - 1 downTo 0) {
            val child = root.getChildAt(i)
            val childResult = findViewAtPoint(child, rawX, rawY)
            if (childResult != null) {
                return childResult
            }
        }
    }

    val composeTarget: TapTarget.ComposeTargetInfo =
        (if (root is AbstractComposeView) {
            resolveInsideComposeView(root, rawX, rawY)
        } else null) ?: return null

    return ViewHitResult(
        composeTarget
    )
}

private fun View.isVisible(): Boolean =
    visibility == View.VISIBLE && alpha > 0f && width > 0 && height > 0

private fun View.containsScreenPoint(rawX: Float, rawY: Float): Boolean {
    val loc = IntArray(2)
    getLocationOnScreen(loc)
    val rect = Rect(loc[0], loc[1], loc[0] + width, loc[1] + height)
    return rect.contains(rawX.toInt(), rawY.toInt())
}

private fun View.toTargetInfo(): TapTarget.ViewTargetInfo {
    val resourceId: String? = runCatching {
        if (id != View.NO_ID) resources.getResourceName(id) else null
    }.getOrNull()

    val text: String? = (this as? TextView)?.text?.toString()?.takeIf { it.isNotEmpty() }

    return TapTarget.ViewTargetInfo(
        className = javaClass.simpleName,
        resourceId = resourceId,
        contentDescription = contentDescription?.toString(),
        text = text,
    )
}
