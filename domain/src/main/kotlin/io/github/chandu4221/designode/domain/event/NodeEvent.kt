package io.github.chandu4221.designode.domain.event

import io.github.chandu4221.designode.domain.model.*

/**
 * Events describing changes to a node tree.
 *
 * Published through [io.github.chandu4221.designode.domain.port.NodeEventPublisher].
 * Named after the aggregate they describe — the tree of [AtomicNode]s.
 */
sealed interface NodeEvent {

    data class NodeInserted(
        val node: AtomicNode,
        val parentId: NodeId,
        val slotId: SlotId,
        val index: Int,
    ) : NodeEvent

    data class NodeRemoved(
        val node: AtomicNode,
        val parentId: NodeId,
        val slotId: SlotId,
        val index: Int,
    ) : NodeEvent

    data class NodeMoved(
        val nodeId: NodeId,
        val fromParentId: NodeId,
        val fromSlotId: SlotId,
        val fromIndex: Int,
        val toParentId: NodeId,
        val toSlotId: SlotId,
        val toIndex: Int,
    ) : NodeEvent

    data class PropertyChanged(
        val nodeId: NodeId,
        val key: PropertyKey,
        val oldValue: Value?,
        val newValue: Value?,
    ) : NodeEvent

    data class VariantChanged(
        val nodeId: NodeId,
        val oldVariant: VariantId?,
        val newVariant: VariantId?,
    ) : NodeEvent

    data class ModifiersChanged(
        val nodeId: NodeId,
        val oldModifiers: List<ModifierToken>,
        val newModifiers: List<ModifierToken>,
    ) : NodeEvent
}