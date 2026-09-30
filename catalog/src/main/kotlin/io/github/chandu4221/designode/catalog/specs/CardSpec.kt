package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec
import io.github.chandu4221.designode.domain.spec.VariantSpec

val CardSpec = ComponentSpec(
    type = ComponentTypeId("Card"),
    family = FamilyId("Containment"),
    level = AtomicLevel.MOLECULE,
    label = "Card",
    variants = listOf(
        VariantSpec(VariantId("filled"), "Filled"),
        VariantSpec(VariantId("elevated"), "Elevated"),
        VariantSpec(VariantId("outlined"), "Outlined"),
    ),
    slots = listOf(
        SlotSpec(
            id = SlotId("content"),
            label = "Content",
            cardinality = Cardinality.OneOrMany,
            isDefault = true,
        ),
    ),
)