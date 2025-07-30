package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import kotlin.math.roundToInt

class InteractionGestureListener(
    val activity: Activity,
) : SimpleOnGestureListener() {
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        val x = event.x.roundToInt()
        val y = event.y.roundToInt()
        recordTouchEvent(TouchEventType.CLICK, activity, x, y)
        return super.onSingleTapUp(event)
    }
}