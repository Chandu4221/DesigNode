package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.PropertySpec

val IconSpec = ComponentSpec(
    type = ComponentTypeId("Icon"),
    family = FamilyId("Basic"),
    level = AtomicLevel.ATOM,
    label = "Icon",
    properties = listOf(
        PropertySpec(
            key = PropertyKey("name"),
            kind = PropertyKind.TEXT,
            label = "Icon name",
            default = Value.Text("Favorite"),
            required = true,
        ),
        PropertySpec(
            key = PropertyKey("contentDescription"),
            kind = PropertyKind.TEXT,
            label = "Content description",
            default = Value.Text(""),
        ),
        PropertySpec(
            key = PropertyKey("tint"),
            kind = PropertyKind.COLOR,
            label = "Tint",
            default = Value.Color(0xFF000000),
        ),
        PropertySpec(
            key = PropertyKey("size"),
            kind = PropertyKind.DP,
            label = "Size",
            default = Value.Dp(24f),
        ),
    ),
)