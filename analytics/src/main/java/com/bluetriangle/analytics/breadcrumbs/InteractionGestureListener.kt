package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.bluetriangle.analytics.breadcrumbs.touchresolver.TapTarget
import com.bluetriangle.analytics.breadcrumbs.touchresolver.TapTargetResolver

internal class InteractionGestureListener(
    val activity: Activity,
    val userEvent: (UserEventType, TapTarget?) -> Unit,
) : SimpleOnGestureListener() {
    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        if(e == null) return super.onSingleTapConfirmed(e)

        userEvent(
            UserEventType.TAP,
            TapTargetResolver.resolve(activity, e.rawX, e.rawY)
        )

        return super.onSingleTapConfirmed(e)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        if(e == null) return super.onDoubleTap(e)

        userEvent(
            UserEventType.DOUBLE_TAP,
            TapTargetResolver.resolve(activity, e.rawX, e.rawY)
        )
        return super.onDoubleTap(e)
    }

}

enum class UserEventType(val value: String) {
    TAP("tap"),
    DOUBLE_TAP("double tap")
}