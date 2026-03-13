@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.bluetriangle.analytics.breadcrumbs.touchresolver

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import java.util.LinkedList
import androidx.compose.ui.node.Owner
import androidx.compose.ui.node.LayoutNode
import com.bluetriangle.analytics.Tracker

object TapTargetResolver {

    private val composeTapTargetDetector = ComposeTapTargetDetector()

    fun resolve(activity: Activity, x: Float, y: Float): TapTarget? {
        val decorView = activity.window?.decorView as? ViewGroup ?: return null

        return findTapTarget(decorView, x, y)
    }

    private fun findTapTarget(
        root: View,
        x: Float,
        y: Float
    ): TapTarget? {
        val queue = LinkedList<View>()
        queue.addFirst(root)

        var target: TapTarget? = null

        while(queue.isNotEmpty()) {
            val current = queue.removeFirst()

            val composeTarget = findComposeTarget(current, x, y)

            if(composeTarget != null) {
                target = composeTarget
                continue
            }

            if(current.isClickableAndVisible()) {
                target = current.toViewTargetInfo(x, y)
            }
            if(current is ViewGroup && current.isVisible) {
                for(i in 0 until current.childCount) {
                    val child = current.getChildAt(i)
                    if(child.isHitTarget(x, y)) {
                        queue.add(child)
                    }
                }
            }
        }

        return target
    }

    private fun findComposeTarget(view: View, x: Float, y: Float): TapTarget? {
        try {
            if(view is Owner) {
                return view.findComposeTarget(x, y)
            }
        } catch (e: Throwable) {
            // We are using internal classes from Compose framework above to track composables automatically.
            // In case the internal classes/fields change, it would throw an exception.
            // The below method is a fallback mechanism which allows us to track elements who are marked by bttTrackAction modifier
            Tracker.instance?.configuration?.logger?.error("Couldn't track composable: ${e.message}: ${e.stackTraceToString()}")
            BttComposeRegistry.hitTest(x, y)?.let {
                return TapTarget.ComposeTarget(it.name, x, y)
            }
        }
        return null
    }

    private fun Owner.findComposeTarget(x: Float, y: Float): TapTarget? {
        val node:LayoutNode? = composeTapTargetDetector.findTapTarget(
            this,
            x,
            y,
        )
        return node?.let {
            TapTarget.ComposeTarget(
                composeTapTargetDetector.nodeToName(node),
                x = x,
                y = y
            )
        }
    }

    private fun View.isClickableAndVisible() = isVisible && isClickable

    private val viewLocation = IntArray(2)

    private fun View.isHitTarget(x: Float, y: Float): Boolean {
        getLocationOnScreen(viewLocation)

        val top = viewLocation[1]
        val left = viewLocation[0]

        val bottom = top + height
        val right = left + width

        return x >= left && y >= top && x <= right && y <= bottom
    }
}