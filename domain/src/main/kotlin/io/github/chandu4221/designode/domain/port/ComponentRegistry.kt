package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.FamilyId
import io.github.chandu4221.designode.domain.spec.ComponentSpec

interface ComponentRegistry {
    fun spec(type: ComponentTypeId): ComponentSpec?
    fun require(type: ComponentTypeId): ComponentSpec
    fun all(): List<ComponentSpec>
    fun byFamily(family: FamilyId): List<ComponentSpec>
}