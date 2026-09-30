package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec
import io.github.chandu4221.designode.domain.spec.VariantSpec

val FloatingActionButtonSpec = ComponentSpec(
    type = ComponentTypeId("FloatingActionButton"),
    family = FamilyId("Actions"),
    level = AtomicLevel.MOLECULE,
    label = "Floating action button",
    variants = listOf(
        VariantSpec(VariantId("standard"), "Standard"),
        VariantSpec(VariantId("small"), "Small"),
        VariantSpec(VariantId("large"), "Large"),
    ),
    slots = listOf(
        SlotSpec(
            id = SlotId("content"),
            label = "Content",
            cardinality = Cardinality.ExactlyOne,
            accepts = setOf(ComponentTypeId("Icon")),
            isDefault = true,
        ),
    ),
)