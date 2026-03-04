package com.bluetriangle.analytics.breadcrumbs.touchresolver

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.AbstractComposeView

object TapTargetResolver {

    fun resolve(activity: Activity, rawX: Float, rawY: Float): TapTarget? {
        val decorView = activity.window?.decorView as? ViewGroup ?: return null

        val contentComposeView = decorView.findSingleComposeContentView()
        if (contentComposeView != null) {
            return resolveInsideComposeView(contentComposeView, rawX, rawY)
        }

        val hitResult = findViewAtPoint(decorView, rawX, rawY) ?: return null

        return hitResult.target
    }
}

private fun ViewGroup.findSingleComposeContentView(): AbstractComposeView? {
    val contentFrame = findViewById<ViewGroup>(android.R.id.content) ?: return null
    if (contentFrame.childCount != 1) return null
    return contentFrame.getChildAt(0) as? AbstractComposeView
}
