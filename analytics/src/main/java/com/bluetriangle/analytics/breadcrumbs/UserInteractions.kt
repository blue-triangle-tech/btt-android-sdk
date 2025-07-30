package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children

enum class TouchEventType(
    val eventName: String,
) {
    TOUCH_BEGAN("Touch Began"),
    TOUCH_ENDED("Touch Ended"),
    CLICK("click"),
}

private class ViewAttributes(
    activity: Activity,
    view: View,
) {
    val className: String? = view.javaClass.canonicalName

    val text: String? = if (view is TextView) view.text.toString() else null

    val accessibilityClassName: String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.accessibilityClassName.toString()
        } else {
            null
        }

    val id: Int? = if (view.id != View.NO_ID) view.id else null

    val idPackage: String? = id?.let { activity.resources.getResourcePackageName(it) }
    val idEntry: String? = id?.let { activity.resources.getResourceEntryName(it) }

    val name: String? get() = idEntry ?: text
}

fun recordTouchEvent(
    type: TouchEventType,
    activity: Activity,
    x: Int,
    y: Int,
) {
    val contentView = activity.findViewById<View>(android.R.id.content)
    val textView = findTextViewAtPosition(contentView, x, y)
    if (textView != null) {
        ViewAttributes(activity, textView)
    }
}

private fun findTextViewAtPosition(
    content: View,
    x: Int,
    y: Int,
): TextView? {
    if (content is ViewGroup) {
        if (content.isShown) {
            // Empirically, this seems to be the order that Android uses to find the touch target,
            // even if the developer has used setZ to override the render order.
            for (child in content.children.toList().reversed()) {
                if (child.isShown) {
                    val view = findTextViewAtPosition(child, x, y)
                    if (view != null) {
                        return view
                    }
                }
            }
        }
    }
    if (content is TextView) {
        val hitRect = Rect()
        content.getHitRect(hitRect)

        val location = IntArray(2)
        content.getLocationInWindow(location)

        val left = location[0]
        val top = location[1]
        val right = left + hitRect.width()
        val bottom = top + hitRect.height()

        val rect = Rect(left, top, right, bottom)

        if (rect.contains(x, y)) {
            return content
        }
    }
    return null
}