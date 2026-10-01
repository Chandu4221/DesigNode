package io.github.chandu4221.designode.domain.model

data class AtomicNode(
    val id: NodeId,
    val type: ComponentTypeId,
    val variant: VariantId? = null,
    val properties: Map<PropertyKey, Value> = emptyMap(),
    val slots: Map<SlotId, SlotContent> = emptyMap(),
    val modifiers: List<ModifierToken> = emptyList(),
)

/**
 * Recursively copies this node and all of its descendants, generating fresh
 * [NodeId]s with [idGenerator] while preserving type, variant, properties, and modifiers.
 */
fun AtomicNode.deepCopyWithNewIds(idGenerator: io.github.chandu4221.designode.domain.port.NodeIdGenerator): AtomicNode = copy(
    id = idGenerator.next(),
    slots = slots.mapValues { (_, content) ->
        content.map { it.deepCopyWithNewIds(idGenerator) }
    },
)