package com.bluetriangle.analytics.breadcrumbs.touchresolver

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
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

fun Modifier.getTrackedName(): String? {
    return (this as? BttTrackActionElement)?.name
}

private data class BttTrackActionElement @SuppressLint("ModifierFactoryReturnType") constructor(
    val name: String
) : ModifierNodeElement<BttTrackActionNode>() {

    override fun create() = BttTrackActionNode(name)

    override fun update(node: BttTrackActionNode) {
        node.name = name
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "bttTrackAction"
        properties["name"] = this@BttTrackActionElement.name
    }
}

private class BttTrackActionNode(
    var name: String
) : Modifier.Node(), GlobalPositionAwareModifierNode {

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val bounds = coordinates.boundsInWindow()

        if (bounds.width > 0 && bounds.height > 0) {
            BttComposeRegistry.register(
                key = name,
                node = TrackedComposableNode(
                    name = name,
                    boundsInWindow = bounds
                )
            )
        }
    }

    override fun onDetach() {
        BttComposeRegistry.unregister(name)
    }
}

fun Modifier.bttTrackAction(name: String): Modifier =
    this.then(BttTrackActionElement(name))
