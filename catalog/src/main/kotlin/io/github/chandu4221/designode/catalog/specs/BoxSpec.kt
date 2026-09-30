package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.PropertySpec
import io.github.chandu4221.designode.domain.spec.SlotSpec

val BoxSpec = ComponentSpec(
    type = ComponentTypeId("Box"),
    family = FamilyId("Layout"),
    level = AtomicLevel.MOLECULE,
    label = "Box",
    slots = listOf(
        SlotSpec(
            id = SlotId("content"),
            label = "Content",
            cardinality = Cardinality.ZeroOrMany,
            isDefault = true,
            scope = LayoutScope.BoxScope,
        ),
    ),
    properties = listOf(
        PropertySpec(
            key = PropertyKey("contentAlignment"),
            kind = PropertyKind.ENUM,
            label = "Content alignment",
            default = Value.EnumValue("topStart"),
            enumOptions = listOf(
                "topStart", "topCenter", "topEnd",
                "centerStart", "center", "centerEnd",
                "bottomStart", "bottomCenter", "bottomEnd",
            ),
        ),
    ),
)