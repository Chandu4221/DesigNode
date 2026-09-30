package io.github.chandu4221.designode.catalog.specs

import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.FamilyId
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec

val SpacerSpec = ComponentSpec(
    type = ComponentTypeId("Spacer"),
    family = FamilyId("Basic"),
    level = AtomicLevel.ATOM,
    label = "Spacer",
    // No properties, no slots. Size comes from ModifierToken.Size.
)