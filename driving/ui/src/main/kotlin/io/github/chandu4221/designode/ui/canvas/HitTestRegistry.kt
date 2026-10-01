package io.github.chandu4221.designode.ui.canvas

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntRect
import io.github.chandu4221.designode.domain.model.NodeId

/**
 * Stores the on-screen bounds of every rendered node. Populated during
 * preview rendering via `onGloballyPositioned`; queried by the overlay
 * for hit-testing.
 *
 * Not thread-safe — Compose desktop renders on a single thread.
 */
class HitTestRegistry {

    private val bounds = mutableMapOf<NodeId, Rect>()

    fun record(id: NodeId, rect: Rect) {
        bounds[id] = rect
    }

    fun clear() {
        bounds.clear()
    }

    /**
     * Finds the deepest (smallest) node whose bounds contain the given
     * point. Returns null if nothing matches.
     */
    fun hitTest(x: Float, y: Float): NodeId? {
        return bounds
            .filterValues { it.contains(androidx.compose.ui.geometry.Offset(x, y)) }
            .minByOrNull { (_, rect) -> rect.width * rect.height }
            ?.key
    }
}