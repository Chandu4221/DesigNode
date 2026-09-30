package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.PropertySpec
import io.github.chandu4221.designode.domain.spec.SlotSpec

val RowSpec = ComponentSpec(
    type = ComponentTypeId("Row"),
    family = FamilyId("Layout"),
    level = AtomicLevel.MOLECULE,
    label = "Row",
    slots = listOf(
        SlotSpec(
            id = SlotId("content"),
            label = "Content",
            cardinality = Cardinality.ZeroOrMany,
            isDefault = true,
            scope = LayoutScope.RowScope,
        ),
    ),
    properties = listOf(
        PropertySpec(
            key = PropertyKey("horizontalArrangement"),
            kind = PropertyKind.ENUM,
            label = "Horizontal arrangement",
            default = Value.EnumValue("start"),
            enumOptions = listOf(
                "start", "end", "center",
                "spaceBetween", "spaceAround", "spaceEvenly",
            ),
        ),
        PropertySpec(
            key = PropertyKey("verticalAlignment"),
            kind = PropertyKind.ENUM,
            label = "Vertical alignment",
            default = Value.EnumValue("top"),
            enumOptions = listOf("top", "centerVertically", "bottom"),
        ),
    ),
)