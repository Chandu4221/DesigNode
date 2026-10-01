package io.github.chandu4221.designode.persistence.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ValueDto {

    @Serializable
    @SerialName("text")
    data class Text(val value: String) : ValueDto

    @Serializable
    @SerialName("bool")
    data class Bool(val value: Boolean) : ValueDto

    @Serializable
    @SerialName("int")
    data class IntValue(val value: Int) : ValueDto

    @Serializable
    @SerialName("dp")
    data class Dp(val value: Float) : ValueDto

    @Serializable
    @SerialName("color")
    data class Color(val value: Long) : ValueDto

    @Serializable
    @SerialName("enum")
    data class EnumValue(val name: String) : ValueDto

    @Serializable
    @SerialName("color_role")
    data class ColorRole(val role: String) : ValueDto
}