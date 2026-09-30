package io.github.chandu4221.designode.persistence.mapper

import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.persistence.dto.ValueDto

fun Value.toDto(): ValueDto = when (this) {
    is Value.Text -> ValueDto.Text(value)
    is Value.Bool -> ValueDto.Bool(value)
    is Value.IntValue -> ValueDto.IntValue(value)
    is Value.Dp -> ValueDto.Dp(value)
    is Value.Color -> ValueDto.Color(value)
    is Value.EnumValue -> ValueDto.EnumValue(name)
}

fun ValueDto.toDomain(): Value = when (this) {
    is ValueDto.Text -> Value.Text(value)
    is ValueDto.Bool -> Value.Bool(value)
    is ValueDto.IntValue -> Value.IntValue(value)
    is ValueDto.Dp -> Value.Dp(value)
    is ValueDto.Color -> Value.Color(value)
    is ValueDto.EnumValue -> Value.EnumValue(name)
}