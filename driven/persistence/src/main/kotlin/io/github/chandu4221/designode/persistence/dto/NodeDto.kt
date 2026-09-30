package io.github.chandu4221.designode.persistence.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NodeDto(
    val id: String,
    val type: String,
    val variant: String? = null,
    val properties: Map<String, ValueDto> = emptyMap(),
    val slots: Map<String, SlotContentDto> = emptyMap(),
    val modifiers: List<ModifierTokenDto> = emptyList(),
)

@Serializable
sealed interface SlotContentDto {

    @Serializable
    @SerialName("empty")
    data object Empty : SlotContentDto

    @Serializable
    @SerialName("one")
    data class One(val node: NodeDto) : SlotContentDto

    @Serializable
    @SerialName("many")
    data class Many(val nodes: List<NodeDto>) : SlotContentDto
}