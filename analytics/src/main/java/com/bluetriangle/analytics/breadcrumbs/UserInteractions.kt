package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.children
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_DURATION
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_END_TIME
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_ENTRY_TYPE
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_FILE
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_START_TIME
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_URL
import org.json.JSONObject

enum class UserEventType {
    TAP,
    DOUBLE_TAP
}

class UserEvent(
    val eventType: UserEventType,
    val x: Int,
    val y: Int,
    val className: String,
    val classFullName: String,
    val id: String?
) {
    private val absoluteStart = System.currentTimeMillis()
    var start = absoluteStart

    fun setNavigationStart(navigationStart: Long) {
        start = absoluteStart - navigationStart
    }

    val payload: JSONObject
        get() = JSONObject(
            mapOf(
                FIELD_ENTRY_TYPE to "UserAction",
                FIELD_URL to StringBuilder().apply {
                    append(classFullName)
                    if(id != null) {
                        append("/id=$id")
                    }
                    append("/x=$x")
                    append("/y=$y")
               }.toString(),
                FIELD_FILE to eventType.name.lowercase(),
                FIELD_START_TIME to start.toString(),
                FIELD_END_TIME to (start + 15).toString(),
                FIELD_DURATION to 15.toString(),
            )
        )
}

private class ViewAttributes(
    activity: Activity,
    view: View
) {
    val className: String = if(view is ComposeView) "" else view.javaClass.simpleName

    val classFullName = if(view is ComposeView) "" else view.javaClass.canonicalName?:view.javaClass.simpleName

    val text: String? = if (view is TextView) view.text.toString() else null

    val accessibilityClassName: String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.accessibilityClassName.toString()
        } else {
            null
        }

    val id: Int? = if (view.id != View.NO_ID) view.id else null

    val stableId: String = when {
        view.id != View.NO_ID -> "${view.resources.getResourcePackageName(view.id)}:${view.resources.getResourceEntryName(view.id)}"
        else -> "view-${System.identityHashCode(view)}"
    }

    val idPackage: String? = id?.let { activity.resources.getResourcePackageName(it) }
    val idEntry: String? = id?.let { activity.resources.getResourceEntryName(it) }
    val accessibilityId: String? = ""

    fun log(touchEvent: UserEventType) {
        Tracker.instance?.configuration?.logger?.debug("""User Interaction ->
            |touchEvent: $touchEvent
            |className: $className
            |accessibilityClassName: $accessibilityClassName
            |accessibilityTitle: $accessibilityId
            |idPackage: $idPackage
            |idEntry: $idEntry
            |id: $id
            |text: $text""".trimMargin())
    }
}

fun recordTouchEvent(
    type: UserEventType,
    activity: Activity,
    x: Int,
    y: Int,
) {
    val contentView = activity.window.decorView.rootView
    val clickableView = findClickableViewAtPosition(contentView, x, y)
    if(clickableView != null) {
        ViewAttributes(activity, clickableView).let {
            val userEvent = UserEvent(
                eventType = type,
                x = x,
                y = y,
                className = it.className,
                classFullName = it.classFullName,
                id = it.stableId
            )
            Tracker.instance?.submitUserEvent(userEvent)
        }
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
    if (view is ComposeView && view.isPointInHitArea(x, y)) {
        return view
    }
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