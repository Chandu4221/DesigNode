package io.github.chandu4221.designode.persistence.mapper

import io.github.chandu4221.designode.domain.model.ModifierToken
import io.github.chandu4221.designode.persistence.dto.ModifierTokenDto

fun ModifierToken.toDto(): ModifierTokenDto = when (this) {
    is ModifierToken.Padding -> ModifierTokenDto.Padding(all)
    is ModifierToken.Size -> ModifierTokenDto.Size(width, height)
    is ModifierToken.FillMaxWidth -> ModifierTokenDto.FillMaxWidth(fraction)
    is ModifierToken.FillMaxHeight -> ModifierTokenDto.FillMaxHeight(fraction)
    is ModifierToken.Weight -> ModifierTokenDto.Weight(value)
}

fun ModifierTokenDto.toDomain(): ModifierToken = when (this) {
    is ModifierTokenDto.Padding -> ModifierToken.Padding(all)
    is ModifierTokenDto.Size -> ModifierToken.Size(width, height)
    is ModifierTokenDto.FillMaxWidth -> ModifierToken.FillMaxWidth(fraction)
    is ModifierTokenDto.FillMaxHeight -> ModifierToken.FillMaxHeight(fraction)
    is ModifierTokenDto.Weight -> ModifierToken.Weight(value)
}