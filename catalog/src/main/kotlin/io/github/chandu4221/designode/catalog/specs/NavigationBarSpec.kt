package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.Cardinality
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.FamilyId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec

val NavigationBarSpec = ComponentSpec(
    type = ComponentTypeId("NavigationBar"),
    family = FamilyId("Bars"),
    level = AtomicLevel.ORGANISM,
    label = "Navigation bar",
    slots = listOf(
        SlotSpec(
            id = SlotId("items"),
            label = "Items",
            cardinality = Cardinality.Range(min = 3, max = 5),
            accepts = setOf(ComponentTypeId("NavigationBarItem")),
            isDefault = true,
        ),
    ),
)