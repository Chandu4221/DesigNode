package io.github.chandu4221.designode.domain.spec

import io.github.chandu4221.designode.domain.model.Cardinality
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.LayoutScope
import io.github.chandu4221.designode.domain.model.SlotId

data class SlotSpec(
    val id: SlotId,
    val label: String,
    val cardinality: Cardinality,
    val accepts: Set<ComponentTypeId> = emptySet(),
    val isDefault: Boolean = false,
    val required: Boolean = false,
    val scope: LayoutScope = LayoutScope.Unscoped,
)