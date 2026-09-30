package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.*

val ButtonSpec = ComponentSpec(
    type = ComponentTypeId("Button"),
    family = FamilyId("Actions"),
    level = AtomicLevel.MOLECULE,
    label = "Button",
    variants = listOf(
        VariantSpec(VariantId("filled"), "Filled"),
        VariantSpec(VariantId("outlined"), "Outlined"),
        VariantSpec(VariantId("text"), "Text"),
        VariantSpec(VariantId("elevated"), "Elevated"),
        VariantSpec(VariantId("tonal"), "Filled tonal"),
    ),
    slots = listOf(
        SlotSpec(
            id = SlotId("content"),
            label = "Content",
            cardinality = Cardinality.OneOrMany,
            accepts = setOf(
                ComponentTypeId("Text"),
                ComponentTypeId("Icon"),
            ),
            isDefault = true,
        ),
    ),
    properties = listOf(
        PropertySpec(
            key = PropertyKey("enabled"),
            kind = PropertyKind.BOOL,
            label = "Enabled",
            default = Value.Bool(true),
        ),
    ),
)