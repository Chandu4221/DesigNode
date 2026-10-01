package io.github.chandu4221.designode.persistence.dto

import kotlinx.serialization.Serializable

@Serializable
data class ThemeSpecDto(
    val seedColor: Long = 0xFF6750A4L,
    val isDark: Boolean = false,
    val contrastLevel: Double = 0.0,
    val style: String = "TonalSpot",
)

@Serializable
data class ProjectDto(
    val id: String,
    val name: String,
    val screens: List<ScreenDto>,
    val startScreenId: String? = null,
    val theme: ThemeSpecDto = ThemeSpecDto(),
)

@Serializable
data class ScreenDto(
    val id: String,
    val name: String,
    val root: NodeDto,
)