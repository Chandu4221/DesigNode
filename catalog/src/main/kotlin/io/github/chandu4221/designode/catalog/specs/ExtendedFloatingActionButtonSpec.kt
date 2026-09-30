package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.PropertySpec
import io.github.chandu4221.designode.domain.spec.SlotSpec

val ExtendedFloatingActionButtonSpec = ComponentSpec(
    type = ComponentTypeId("ExtendedFloatingActionButton"),
    family = FamilyId("Actions"),
    level = AtomicLevel.MOLECULE,
    label = "Extended floating action button",
    slots = listOf(
        SlotSpec(
            id = SlotId("icon"),
            label = "Icon",
            cardinality = Cardinality.ExactlyOne,
            accepts = setOf(ComponentTypeId("Icon")),
        ),
        SlotSpec(
            id = SlotId("text"),
            label = "Text",
            cardinality = Cardinality.ExactlyOne,
            accepts = setOf(ComponentTypeId("Text")),
        ),
    ),
    properties = listOf(
        PropertySpec(
            key = PropertyKey("expanded"),
            kind = PropertyKind.BOOL,
            label = "Expanded",
            default = Value.Bool(true),
        ),
    ),
)