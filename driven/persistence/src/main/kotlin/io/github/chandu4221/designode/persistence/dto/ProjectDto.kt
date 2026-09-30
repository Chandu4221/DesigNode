package io.github.chandu4221.designode.persistence.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDto(
    val id: String,
    val name: String,
    val screens: List<ScreenDto>,
    val startScreenId: String? = null,
)

@Serializable
data class ScreenDto(
    val id: String,
    val name: String,
    val root: NodeDto,
)