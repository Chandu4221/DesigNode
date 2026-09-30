package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.Cardinality
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.FamilyId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec

val ScaffoldSpec = ComponentSpec(
    type = ComponentTypeId("Scaffold"),
    family = FamilyId("Layout"),
    level = AtomicLevel.ORGANISM,
    label = "Scaffold",
    slots = listOf(
        SlotSpec(
            id = SlotId("topBar"),
            label = "Top bar",
            cardinality = Cardinality.ZeroOrOne,
            accepts = setOf(ComponentTypeId("TopAppBar")),
        ),
        SlotSpec(
            id = SlotId("bottomBar"),
            label = "Bottom bar",
            cardinality = Cardinality.ZeroOrOne,
            accepts = setOf(ComponentTypeId("NavigationBar")),
        ),
        SlotSpec(
            id = SlotId("snackbarHost"),
            label = "Snackbar host",
            cardinality = Cardinality.ZeroOrOne,
        ),
        SlotSpec(
            id = SlotId("floatingActionButton"),
            label = "Floating action button",
            cardinality = Cardinality.ZeroOrOne,
            accepts = setOf(ComponentTypeId("FloatingActionButton")),
        ),
        SlotSpec(
            id = SlotId("content"),
            label = "Content",
            cardinality = Cardinality.ExactlyOne,
            isDefault = true,
        ),
    ),
)