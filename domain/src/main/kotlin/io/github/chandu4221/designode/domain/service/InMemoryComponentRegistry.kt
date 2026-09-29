package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.FamilyId
import io.github.chandu4221.designode.domain.port.ComponentRegistry
import io.github.chandu4221.designode.domain.spec.ComponentSpec

class InMemoryComponentRegistry(
    specs: List<ComponentSpec>,
) : ComponentRegistry {

    private val byType: Map<ComponentTypeId, ComponentSpec> =
        specs.associateBy { it.type }

    init {
        require(byType.size == specs.size) {
            "Duplicate ComponentTypeId in registry: ${specs.map { it.type.value }}"
        }
    }

    override fun spec(type: ComponentTypeId): ComponentSpec? = byType[type]

    override fun require(type: ComponentTypeId): ComponentSpec =
        byType[type] ?: error("Unknown component type: ${type.value}")

    override fun all(): List<ComponentSpec> = byType.values.toList()

    override fun byFamily(family: FamilyId): List<ComponentSpec> =
        byType.values.filter { it.family == family }
}