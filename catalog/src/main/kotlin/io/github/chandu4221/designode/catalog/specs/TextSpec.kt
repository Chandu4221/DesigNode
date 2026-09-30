package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.PropertySpec

val TextSpec = ComponentSpec(
    type = ComponentTypeId("Text"),
    family = FamilyId("Basic"),
    level = AtomicLevel.ATOM,
    label = "Text",
    properties = listOf(
        PropertySpec(
            key = PropertyKey("text"),
            kind = PropertyKind.TEXT,
            label = "Text",
            default = Value.Text(""),
            required = true,
        ),
        PropertySpec(
            key = PropertyKey("style"),
            kind = PropertyKind.ENUM,
            label = "Style",
            default = Value.EnumValue("bodyMedium"),
            enumOptions = listOf(
                "displayLarge", "displayMedium", "displaySmall",
                "headlineLarge", "headlineMedium", "headlineSmall",
                "titleLarge", "titleMedium", "titleSmall",
                "bodyLarge", "bodyMedium", "bodySmall",
                "labelLarge", "labelMedium", "labelSmall",
            ),
        ),
        PropertySpec(
            key = PropertyKey("color"),
            kind = PropertyKind.COLOR,
            label = "Color",
            default = Value.Color(0xFF000000),
        ),
        PropertySpec(
            key = PropertyKey("textAlign"),
            kind = PropertyKind.ENUM,
            label = "Align",
            default = Value.EnumValue("start"),
            enumOptions = listOf("start", "center", "end", "justify"),
        ),
    ),
)