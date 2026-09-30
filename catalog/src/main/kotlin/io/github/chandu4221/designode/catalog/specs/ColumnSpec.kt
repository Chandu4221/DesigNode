package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.PropertySpec
import io.github.chandu4221.designode.domain.spec.SlotSpec

val ColumnSpec = ComponentSpec(
    type = ComponentTypeId("Column"),
    family = FamilyId("Layout"),
    level = AtomicLevel.MOLECULE,
    label = "Column",
    slots = listOf(
        SlotSpec(
            id = SlotId("content"),
            label = "Content",
            cardinality = Cardinality.ZeroOrMany,
            isDefault = true,
            scope = LayoutScope.ColumnScope,
        ),
    ),
    properties = listOf(
        PropertySpec(
            key = PropertyKey("verticalArrangement"),
            kind = PropertyKind.ENUM,
            label = "Vertical arrangement",
            default = Value.EnumValue("top"),
            enumOptions = listOf(
                "top", "bottom", "center",
                "spaceBetween", "spaceAround", "spaceEvenly",
            ),
        ),
        PropertySpec(
            key = PropertyKey("horizontalAlignment"),
            kind = PropertyKind.ENUM,
            label = "Horizontal alignment",
            default = Value.EnumValue("start"),
            enumOptions = listOf("start", "centerHorizontally", "end"),
        ),
    ),
)