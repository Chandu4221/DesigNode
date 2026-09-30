package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.event.NodeEvent
import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.port.ComponentRegistry
import io.github.chandu4221.designode.domain.port.NodeEventPublisher
import io.github.chandu4221.designode.domain.port.NodeIdGenerator
import io.github.chandu4221.designode.domain.port.TreeEditor
import io.github.chandu4221.designode.domain.port.TreeQuery
import io.github.chandu4221.designode.domain.model.Cardinality

class NodeTree(
    private val registry: ComponentRegistry,
    private val validator: SlotValidator,
    private val publisher: NodeEventPublisher,
    private val idGenerator: NodeIdGenerator,
    root: AtomicNode,
) : TreeQuery, TreeEditor {

    override var root: AtomicNode = root
        private set

    // ─────────────────────────────────────────────────────
    // Query
    // ─────────────────────────────────────────────────────

    override fun find(id: NodeId): AtomicNode? = findIn(root, id)

    override fun parentOf(id: NodeId): AtomicNode? = parentIn(root, id)

    override fun slotOf(parentId: NodeId, childId: NodeId): SlotId? {
        val parent = find(parentId) ?: return null
        return parent.slots.entries
            .firstOrNull { (_, content) -> content.nodes().any { it.id == childId } }
            ?.key
    }

    // ─────────────────────────────────────────────────────
    // Mutations
    // ─────────────────────────────────────────────────────

    override fun insert(
        parentId: NodeId,
        slotId: SlotId,
        type: ComponentTypeId,
        variant: VariantId?,
        properties: Map<PropertyKey, Value>,
        modifiers: List<ModifierToken>,
        index: Int?,
    ): Result<NodeId> {
        val parent = find(parentId)
            ?: return failure("Parent not found: ${parentId.value}")

        val child = AtomicNode(
            id = idGenerator.next(),
            type = type,
            variant = variant,
            properties = properties,
            modifiers = modifiers,
        )

        validator.canDrop(parent, slotId, child).onFailure { return Result.failure(it) }

        root = rewrite(root, parentId) { p ->
            val existing = p.slots[slotId]?.nodes().orEmpty()
            val updated = if (index != null && index in 0..existing.size) {
                existing.toMutableList().apply { add(index, child) }
            } else {
                existing + child
            }
            p.copy(slots = p.slots + (slotId to SlotContent.of(updated)))
        }

        publisher.publish(
            NodeEvent.NodeInserted(
                node = child,
                parentId = parentId,
                slotId = slotId,
                index = index ?: (root.slots[slotId]?.nodes()?.size?.minus(1) ?: 0),
            )
        )
        return Result.success(child.id)
    }

    override fun remove(nodeId: NodeId): Result<Unit> {
        if (nodeId == root.id) return failure("Cannot remove root")

        val node = find(nodeId) ?: return failure("Node not found: ${nodeId.value}")
        val parent = parentOf(nodeId) ?: return failure("Parent not found")
        val slotId = slotOf(parent.id, nodeId) ?: return failure("Slot not found")

        val existing = parent.slots[slotId]?.nodes().orEmpty()
        val index = existing.indexOfFirst { it.id == nodeId }

        root = rewrite(root, parent.id) { p ->
            val kept = (p.slots[slotId]?.nodes().orEmpty()).filterNot { it.id == nodeId }
            p.copy(slots = p.slots + (slotId to SlotContent.of(kept)))
        }

        publisher.publish(
            NodeEvent.NodeRemoved(
                node = node,
                parentId = parent.id,
                slotId = slotId,
                index = index,
            )
        )
        return Result.success(Unit)
    }

    override fun move(
        nodeId: NodeId,
        toParentId: NodeId,
        toSlotId: SlotId,
        toIndex: Int?,
    ): Result<Unit> {
        if (nodeId == root.id) return failure("Cannot move root")

        val node = find(nodeId) ?: return failure("Node not found")
        val fromParent = parentOf(nodeId) ?: return failure("Source parent not found")
        val fromSlotId = slotOf(fromParent.id, nodeId) ?: return failure("Source slot not found")
        val toParent = find(toParentId) ?: return failure("Target parent not found")

        if (nodeId == toParentId) return failure("Cannot move node into itself")

        val fromContent = fromParent.slots[fromSlotId]?.nodes().orEmpty()
        val fromIndex = fromContent.indexOfFirst { it.id == nodeId }
        val sameSlot = fromParent.id == toParentId && fromSlotId == toSlotId

        val strippedNode = stripInvalidModifiers(node, toParent, toSlotId)
        if (!sameSlot) {
            val siblings = toParent.slots[toSlotId]?.nodes().orEmpty()
            val targetSpec = registry.require(toParent.type).slots.first { it.id == toSlotId }
            val wouldViolateCardinality =
                siblings.size >= 1 && targetSpec.cardinality in singleCardinalities
            if (wouldViolateCardinality) return failure("Slot '${toSlotId.value}' is full")

            validator.canDrop(toParent, toSlotId, strippedNode).onFailure { return Result.failure(it) }
        }

        root = rewrite(root, fromParent.id) { p ->
            val kept = (p.slots[fromSlotId]?.nodes().orEmpty()).filterNot { it.id == nodeId }
            p.copy(slots = p.slots + (fromSlotId to SlotContent.of(kept)))
        }

        root = rewrite(root, toParentId) { p ->
            val existing = p.slots[toSlotId]?.nodes().orEmpty()
            val updated = if (toIndex != null && toIndex in 0..existing.size) {
                existing.toMutableList().apply { add(toIndex, strippedNode) }
            } else {
                existing + strippedNode
            }
            p.copy(slots = p.slots + (toSlotId to SlotContent.of(updated)))
        }

        publisher.publish(
            NodeEvent.NodeMoved(
                nodeId = nodeId,
                fromParentId = fromParent.id,
                fromSlotId = fromSlotId,
                fromIndex = fromIndex,
                toParentId = toParentId,
                toSlotId = toSlotId,
                toIndex = toIndex ?: 0,
            )
        )

        val stripped = node.modifiers.filterNot { it in strippedNode.modifiers }
        if (stripped.isNotEmpty()) {
            publisher.publish(
                NodeEvent.ModifiersChanged(
                    nodeId = nodeId,
                    oldModifiers = node.modifiers,
                    newModifiers = strippedNode.modifiers,
                )
            )
        }

        return Result.success(Unit)
    }

    override fun updateProperty(nodeId: NodeId, key: PropertyKey, value: Value?): Result<Unit> {
        val node = find(nodeId) ?: return failure("Node not found")
        val old = node.properties[key]

        root = rewrite(root, nodeId) { n ->
            val updated = if (value == null) n.properties - key else n.properties + (key to value)
            n.copy(properties = updated)
        }

        publisher.publish(NodeEvent.PropertyChanged(nodeId, key, old, value))
        return Result.success(Unit)
    }

    override fun updateVariant(nodeId: NodeId, variant: VariantId?): Result<Unit> {
        val node = find(nodeId) ?: return failure("Node not found")
        val old = node.variant

        root = rewrite(root, nodeId) { it.copy(variant = variant) }

        publisher.publish(NodeEvent.VariantChanged(nodeId, old, variant))
        return Result.success(Unit)
    }

    override fun updateModifiers(nodeId: NodeId, modifiers: List<ModifierToken>): Result<Unit> {
        val node = find(nodeId) ?: return failure("Node not found")
        val old = node.modifiers

        root = rewrite(root, nodeId) { it.copy(modifiers = modifiers) }

        publisher.publish(NodeEvent.ModifiersChanged(nodeId, old, modifiers))
        return Result.success(Unit)
    }

    // ─────────────────────────────────────────────────────
    // Internals
    // ─────────────────────────────────────────────────────

    private val singleCardinalities =
        setOf(Cardinality.ZERO_OR_ONE, Cardinality.EXACTLY_ONE)

    private fun stripInvalidModifiers(
        node: AtomicNode,
        targetParent: AtomicNode,
        targetSlot: SlotId,
    ): AtomicNode {
        val slotSpec = registry.spec(targetParent.type)
            ?.slots?.firstOrNull { it.id == targetSlot }
            ?: return node
        val valid = node.modifiers.filter { modifier ->
            slotSpec.scope in modifier.allowedScopes ||
                    LayoutScope.Unscoped in modifier.allowedScopes
        }
        return if (valid.size == node.modifiers.size) node else node.copy(modifiers = valid)
    }

    private fun findIn(node: AtomicNode, id: NodeId): AtomicNode? {
        if (node.id == id) return node
        for (content in node.slots.values) {
            for (child in content.nodes()) {
                findIn(child, id)?.let { return it }
            }
        }
        return null
    }

    private fun parentIn(node: AtomicNode, id: NodeId): AtomicNode? {
        for (content in node.slots.values) {
            for (child in content.nodes()) {
                if (child.id == id) return node
                parentIn(child, id)?.let { return it }
            }
        }
        return null
    }

    private fun rewrite(
        node: AtomicNode,
        targetId: NodeId,
        transform: (AtomicNode) -> AtomicNode,
    ): AtomicNode {
        if (node.id == targetId) return transform(node)
        return node.copy(
            slots = node.slots.mapValues { (_, content) ->
                content.map { child -> rewrite(child, targetId, transform) }
            }
        )
    }

    private fun failure(message: String): Result<Nothing> =
        Result.failure(IllegalArgumentException(message))
}