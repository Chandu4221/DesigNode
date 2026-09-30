package io.github.chandu4221.designode.persistence.mapper

import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.persistence.dto.SlotContentDto

fun SlotContent.toDto(): SlotContentDto = when (this) {
    SlotContent.Empty -> SlotContentDto.Empty
    is SlotContent.One -> SlotContentDto.One(node.toDto())
    is SlotContent.Many -> SlotContentDto.Many(nodes.map { it.toDto() })
}

fun SlotContentDto.toDomain(): SlotContent = when (this) {
    SlotContentDto.Empty -> SlotContent.Empty
    is SlotContentDto.One -> SlotContent.One(node.toDomain())
    is SlotContentDto.Many -> SlotContent.Many(nodes.map { it.toDomain() })
}