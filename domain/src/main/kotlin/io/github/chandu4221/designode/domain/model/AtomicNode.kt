package io.github.chandu4221.designode.domain.model

data class AtomicNode(
    val id: NodeId,
    val type: ComponentTypeId,
    val variant: VariantId? = null,
    val properties: Map<PropertyKey, Value> = emptyMap(),
    val slots: Map<SlotId, SlotContent> = emptyMap(),
    val modifiers: List<ModifierToken> = emptyList(),
)