package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.bluetriangle.analytics.breadcrumbs.touchresolver.TapTarget
import com.bluetriangle.analytics.breadcrumbs.touchresolver.TapTargetResolver

internal class InteractionGestureListener(
    val activity: Activity,
    val userEvent: (UserEventType, TapTarget) -> Unit,
) : SimpleOnGestureListener() {
    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        if(e == null) return super.onSingleTapConfirmed(e)

        TapTargetResolver.resolve(activity, e.rawX, e.rawY)?.let {
            userEvent(UserEventType.TAP, it)
        }

        return super.onSingleTapConfirmed(e)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        if(e == null) return super.onDoubleTap(e)

        TapTargetResolver.resolve(activity, e.rawX, e.rawY)?.let {
            userEvent(UserEventType.DOUBLE_TAP, it)
        }
        return super.onDoubleTap(e)
    }

}

enum class UserEventType(val value: String) {
    TAP("tap"),
    DOUBLE_TAP("double tap")
}