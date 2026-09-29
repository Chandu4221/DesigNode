package io.github.chandu4221.designode.domain.model

sealed interface Value {
    data class Text(val value: String) : Value
    data class Bool(val value: Boolean) : Value
    data class IntValue(val value: Int) : Value
    data class Dp(val value: Float) : Value
    data class Color(val value: Long) : Value      // ARGB as Long
    data class EnumValue(val name: String) : Value
}