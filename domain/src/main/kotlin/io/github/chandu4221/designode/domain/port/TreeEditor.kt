package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.ModifierToken
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.VariantId

/**
 * Write surface for a node tree.
 *
 * All mutating operations return [Result]. On success, a domain event has
 * already been published. On failure, the tree is unchanged.
 */
interface TreeEditor {

    /**
     * Creates a new node of [type] inside [slotId] of [parentId].
     * Returns the new node's ID on success.
     */
    fun insert(
        parentId: NodeId,
        slotId: SlotId,
        type: ComponentTypeId,
        variant: VariantId? = null,
        properties: Map<PropertyKey, Value> = emptyMap(),
        modifiers: List<ModifierToken> = emptyList(),
        index: Int? = null,
    ): Result<NodeId>

    /** Removes [nodeId] and its subtree. Cannot remove the root. */
    fun remove(nodeId: NodeId): Result<Unit>

    /**
     * Moves [nodeId] to a new parent, slot, or position.
     * Invalid modifiers for the destination scope are stripped automatically.
     */
    fun move(
        nodeId: NodeId,
        toParentId: NodeId,
        toSlotId: SlotId,
        toIndex: Int? = null,
    ): Result<Unit>

    /** Sets or removes (when [value] is null) a property on [nodeId]. */
    fun updateProperty(nodeId: NodeId, key: PropertyKey, value: Value?): Result<Unit>

    /** Changes the visual variant of [nodeId]. Pass null for the default. */
    fun updateVariant(nodeId: NodeId, variant: VariantId?): Result<Unit>

    /** Replaces the full modifier chain of [nodeId]. */
    fun updateModifiers(nodeId: NodeId, modifiers: List<ModifierToken>): Result<Unit>
}