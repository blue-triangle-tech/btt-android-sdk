package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.bluetriangle.analytics.Tracker

enum class TouchEventType {
    TAP,
    DOUBLE_TAP
}

private class ViewAttributes(
    activity: Activity,
    view: View,
    val contentText: String? = null
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
    val accessibilityTitle: String? = view.accessibilityNodeProvider?.createAccessibilityNodeInfo(0)?.text?.toString()

    val name: String? get() = idEntry ?: text

    fun log(touchEvent: TouchEventType) {
        Tracker.instance?.configuration?.logger?.debug("""User Interaction -> 
            |touchEvent: $touchEvent
            |className: $className
            |accessibilityClassName: $accessibilityClassName
            |accessibilityTitle: $accessibilityTitle
            |idPackage: $idPackage
            |idEntry: $idEntry
            |id: $id
            |name: $name
            |text: $text
            |firstText: $contentText""".trimMargin())
    }
}

fun recordTouchEvent(
    type: TouchEventType,
    activity: Activity,
    x: Int,
    y: Int,
) {
    val contentView = activity.findViewById<View>(android.R.id.content)
    val textView = findTextViewAtPosition(contentView, x, y)
    val clickableView = findClickableViewAtPosition(contentView, x, y)
    if (textView != null) {
        ViewAttributes(activity, textView).log(type)
    }
    if(clickableView != null) {
        val firstText = findTextChild(clickableView)
        ViewAttributes(activity, clickableView, firstText).log(type)
    }
}

fun findTextChild(view: View): String? {
    if(view is ViewGroup) {
        for(child in view.children.toList().reversed()) {
            if(child is TextView) {
                return child.text?.toString()
            } else {
                val text = findTextChild(child)
                if(text != null) {
                    return text
                }
            }
        }
    }
    return null
}

fun findClickableViewAtPosition(view: View, x: Int, y: Int): View? {
    if(view.isShown && view.isClickable) {
        val hitRect = Rect()
        view.getHitRect(hitRect)

        val location = IntArray(2)
        view.getLocationInWindow(location)

        val left = location[0]
        val top = location[1]
        val right = left + hitRect.width()
        val bottom = top + hitRect.height()

        val rect = Rect(left, top, right, bottom)

        if (rect.contains(x, y)) {
            return view
        }
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

        if (content.isClickable && rect.contains(x, y)) {
            return content
        }
    }
    return null
}