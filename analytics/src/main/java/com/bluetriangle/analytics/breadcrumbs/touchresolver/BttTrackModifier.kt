package com.bluetriangle.analytics.breadcrumbs.touchresolver

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.debugInspectorInfo
import java.util.concurrent.ConcurrentHashMap

data class TrackedComposableNode(
    val name: String,
    val boundsInWindow: Rect,
)

object BttComposeRegistry {

    private val nodes = ConcurrentHashMap<String, TrackedComposableNode>()

    internal fun register(key: String, node: TrackedComposableNode) {
        nodes[key] = node
    }

    internal fun unregister(key: String) {
        nodes.remove(key)
    }

    internal fun hitTest(rawX: Float, rawY: Float): TrackedComposableNode? {
        val point = Offset(rawX, rawY)
        return nodes.values
            .filter { it.boundsInWindow.contains(point) }
            .minByOrNull { it.boundsInWindow.width * it.boundsInWindow.height }
    }

    fun clear() = nodes.clear()
}

fun Modifier.bttTrackAction(
    name: String
): Modifier = composed {
    val registryKey = remember(name) { name }

    DisposableEffect(registryKey) {
        onDispose { BttComposeRegistry.unregister(registryKey) }
    }

    onGloballyPositioned { coords ->
        val bounds = coords.boundsInWindow()

        if (bounds.width > 0f && bounds.height > 0f) {
            BttComposeRegistry.register(
                key = registryKey,
                node = TrackedComposableNode(
                    name = name,
                    boundsInWindow = bounds,
                )
            )
        }
    }
}
