package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children

enum class TouchEventType {
    TAP,
    DOUBLE_TAP
}

private class ViewAttributes(
    activity: Activity,
    view: View
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
    val accessibilityId: String? = view.createAccessibilityNodeInfo()?.text?.toString()

    fun log(touchEvent: TouchEventType) {
//        Tracker.instance?.configuration?.logger?.debug("""User Interaction ->
//            |touchEvent: $touchEvent
//            |className: $className
//            |accessibilityClassName: $accessibilityClassName
//            |accessibilityTitle: $accessibilityTitle
//            |idPackage: $idPackage
//            |idEntry: $idEntry
//            |id: $id
//            |name: $name
//            |text: $text
//            |firstText: $contentText""".trimMargin())
    }
}

fun recordTouchEvent(
    type: TouchEventType,
    activity: Activity,
    x: Int,
    y: Int,
) {
    val contentView = activity.window.decorView.rootView
    val clickableView = findClickableViewAtPosition(contentView, x, y)
    if(clickableView != null) {
        ViewAttributes(activity, clickableView).log(type)
    }
}

private fun View.isPointInHitArea(x: Int, y: Int): Boolean {
    val hitRect = Rect()
    getHitRect(hitRect)

    val location = IntArray(2)
    getLocationInWindow(location)

    val left = location[0]
    val top = location[1]
    val right = left + hitRect.width()
    val bottom = top + hitRect.height()

    val rect = Rect(left, top, right, bottom)

    return rect.contains(x, y)
}

fun findClickableViewAtPosition(view: View, x: Int, y: Int): View? {
    if(view.isShown && view.isClickable && view.isPointInHitArea(x, y)) {
        return view
    }

    if (view is ViewGroup) {
        if (view.isShown) {
            // Empirically, this seems to be the order that Android uses to find the touch target,
            // even if the developer has used setZ to override the render order.
            for (child in view.children.toList().reversed()) {
                if (child.isShown) {
                    val view = findClickableViewAtPosition(child, x, y)
                    if (view != null) {
                        return view
                    }
                }
            }
        }
    }
    return null
}