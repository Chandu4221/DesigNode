package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.Cardinality
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.FamilyId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec

val NavigationBarItemSpec = ComponentSpec(
    type = ComponentTypeId("NavigationBarItem"),
    family = FamilyId("Bars"),
    level = AtomicLevel.MOLECULE,
    label = "Navigation item",
    slots = listOf(
        SlotSpec(
            id = SlotId("icon"),
            label = "Icon",
            cardinality = Cardinality.ExactlyOne,
            accepts = setOf(ComponentTypeId("Icon")),
        ),
        SlotSpec(
            id = SlotId("label"),
            label = "Label",
            cardinality = Cardinality.ZeroOrOne,
            accepts = setOf(ComponentTypeId("Text")),
        ),
        SlotSpec(
            id = SlotId("badge"),
            label = "Badge",
            cardinality = Cardinality.ZeroOrOne,
        ),
    ),
)