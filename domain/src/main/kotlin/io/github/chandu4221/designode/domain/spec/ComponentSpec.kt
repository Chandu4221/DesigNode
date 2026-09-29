package io.github.chandu4221.designode.domain.spec

import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.FamilyId

data class ComponentSpec(
    val type: ComponentTypeId,
    val family: FamilyId,
    val level: AtomicLevel,
    val label: String,
    val variants: List<VariantSpec> = emptyList(),
    val slots: List<SlotSpec> = emptyList(),
    val properties: List<PropertySpec> = emptyList(),
)