package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import kotlin.math.roundToInt

internal class InteractionGestureListener(
    val activity: Activity,
) : SimpleOnGestureListener() {
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        val x = event.x.roundToInt()
        val y = event.y.roundToInt()
        recordTouchEvent(UserEventType.TAP, activity, x, y)
        return super.onSingleTapUp(event)
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        if(e == null) return super.onDoubleTap(e)
        val x = e.x.roundToInt()
        val y = e.y.roundToInt()
        recordTouchEvent(UserEventType.DOUBLE_TAP, activity, x, y)
        return super.onDoubleTap(e)
    }
}