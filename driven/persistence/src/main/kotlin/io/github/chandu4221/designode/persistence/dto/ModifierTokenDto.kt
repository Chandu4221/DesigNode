package io.github.chandu4221.designode.persistence.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ModifierTokenDto {

    @Serializable
    @SerialName("padding")
    data class Padding(val all: Float) : ModifierTokenDto

    @Serializable
    @SerialName("size")
    data class Size(
        val width: Float? = null,
        val height: Float? = null,
    ) : ModifierTokenDto

    @Serializable
    @SerialName("fillMaxWidth")
    data class FillMaxWidth(val fraction: Float = 1f) : ModifierTokenDto

    @Serializable
    @SerialName("fillMaxHeight")
    data class FillMaxHeight(val fraction: Float = 1f) : ModifierTokenDto

    @Serializable
    @SerialName("weight")
    data class Weight(val value: Float) : ModifierTokenDto
}