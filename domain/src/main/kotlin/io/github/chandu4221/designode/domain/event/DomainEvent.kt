package io.github.chandu4221.designode.domain.event

import io.github.chandu4221.designode.domain.model.*

sealed interface DomainEvent {

    data class NodeInserted(
        val node: AtomicNode,        // full subtree, self-contained
        val parentId: NodeId,
        val slotId: SlotId,
        val index: Int,
    ) : DomainEvent

    data class NodeRemoved(
        val node: AtomicNode,        // full subtree, needed for undo
        val parentId: NodeId,
        val slotId: SlotId,
        val index: Int,
    ) : DomainEvent

    data class NodeMoved(
        val nodeId: NodeId,
        val fromParentId: NodeId,
        val fromSlotId: SlotId,
        val fromIndex: Int,
        val toParentId: NodeId,
        val toSlotId: SlotId,
        val toIndex: Int,
    ) : DomainEvent

    data class PropertyChanged(
        val nodeId: NodeId,
        val key: PropertyKey,
        val oldValue: Value?,
        val newValue: Value?,
    ) : DomainEvent

    data class VariantChanged(
        val nodeId: NodeId,
        val oldVariant: VariantId?,
        val newVariant: VariantId?,
    ) : DomainEvent

    data class ModifiersChanged(
        val nodeId: NodeId,
        val oldModifiers: List<ModifierToken>,
        val newModifiers: List<ModifierToken>,
    ) : DomainEvent
}