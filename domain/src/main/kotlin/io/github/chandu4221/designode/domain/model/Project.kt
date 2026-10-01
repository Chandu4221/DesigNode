package io.github.chandu4221.designode.domain.model

data class Project(
    val id: ProjectId,
    val name: String,
    val screens: Map<ScreenId, Screen>,
    val startScreenId: ScreenId?,
    val theme: ThemeSpec = ThemeSpec.Default,
)