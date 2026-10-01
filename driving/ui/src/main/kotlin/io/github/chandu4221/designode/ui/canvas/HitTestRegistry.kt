package io.github.chandu4221.designode.ui.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.SlotId

/** Identifies a specific slot on a specific parent node. */
data class SlotTarget(
    val nodeId: NodeId,
    val slotId: SlotId,
)

/**
 * Stores the on-screen bounds of every rendered node and dedicated slot drop zones.
 * Populated during preview rendering via `onGloballyPositioned`; queried by the overlay
 * and drag-and-drop system for hit-testing.
 *
 * Not thread-safe — Compose desktop renders on a single thread.
 */
class HitTestRegistry {

    private val bounds = mutableMapOf<NodeId, Rect>()
    private val slotBounds = mutableMapOf<SlotTarget, Rect>()

    fun record(id: NodeId, rect: Rect) {
        bounds[id] = rect
    }

    fun recordSlot(nodeId: NodeId, slotId: SlotId, rect: Rect) {
        slotBounds[SlotTarget(nodeId, slotId)] = rect
    }

    fun clear() {
        bounds.clear()
        slotBounds.clear()
    }

    /**
     * Finds the deepest (smallest) node whose bounds contain the given point.
     * Returns null if nothing matches.
     */
    fun hitTest(x: Float, y: Float): NodeId? {
        return bounds
            .filterValues { it.contains(Offset(x, y)) }
            .minByOrNull { (_, rect) -> rect.width * rect.height }
            ?.key
    }

    /**
     * Finds the deepest (smallest) slot drop zone whose bounds contain the given point.
     * Returns null if no slot zone matches.
     */
    fun hitTestSlot(x: Float, y: Float): SlotTarget? {
        return slotBounds
            .filterValues { it.contains(Offset(x, y)) }
            .minByOrNull { (_, rect) -> rect.width * rect.height }
            ?.key
    }
}