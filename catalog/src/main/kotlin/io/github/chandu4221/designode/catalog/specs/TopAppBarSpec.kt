package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec
import io.github.chandu4221.designode.domain.spec.VariantSpec

val TopAppBarSpec = ComponentSpec(
    type = ComponentTypeId("TopAppBar"),
    family = FamilyId("Bars"),
    level = AtomicLevel.ORGANISM,
    label = "Top app bar",
    variants = listOf(
        VariantSpec(VariantId("small"), "Small"),
        VariantSpec(VariantId("centerAligned"), "Center aligned"),
        VariantSpec(VariantId("medium"), "Medium"),
        VariantSpec(VariantId("large"), "Large"),
    ),
    slots = listOf(
        SlotSpec(
            id = SlotId("title"),
            label = "Title",
            cardinality = Cardinality.ExactlyOne,
        ),
        SlotSpec(
            id = SlotId("navigationIcon"),
            label = "Navigation icon",
            cardinality = Cardinality.ZeroOrOne,
        ),
        SlotSpec(
            id = SlotId("actions"),
            label = "Actions",
            cardinality = Cardinality.ZeroOrMany,
        ),
    ),
)