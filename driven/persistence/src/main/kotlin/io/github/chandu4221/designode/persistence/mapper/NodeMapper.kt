package io.github.chandu4221.designode.persistence.mapper

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.ModifierToken
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.VariantId
import io.github.chandu4221.designode.persistence.dto.NodeDto

fun AtomicNode.toDto(): NodeDto = NodeDto(
    id = id.value,
    type = type.value,
    variant = variant?.value,
    properties = properties.entries.associate { (k, v) -> k.value to v.toDto() },
    slots = slots.entries.associate { (k, v) -> k.value to v.toDto() },
    modifiers = modifiers.map { it.toDto() },
)

fun NodeDto.toDomain(): AtomicNode = AtomicNode(
    id = NodeId(id),
    type = ComponentTypeId(type),
    variant = variant?.let { VariantId(it) },
    properties = properties.entries.associate { (k, v) ->
        PropertyKey(k) to v.toDomain()
    },
    slots = slots.entries.associate { (k, v) ->
        SlotId(k) to v.toDomain()
    },
    modifiers = modifiers.map { it.toDomain() },
)