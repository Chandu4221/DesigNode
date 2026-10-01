package io.github.chandu4221.designode.persistence.mapper

import io.github.chandu4221.designode.domain.model.Project
import io.github.chandu4221.designode.domain.model.ProjectId
import io.github.chandu4221.designode.domain.model.Screen
import io.github.chandu4221.designode.domain.model.ScreenId
import io.github.chandu4221.designode.domain.model.ThemeSpec
import io.github.chandu4221.designode.persistence.dto.ProjectDto
import io.github.chandu4221.designode.persistence.dto.ScreenDto
import io.github.chandu4221.designode.persistence.dto.ThemeSpecDto

fun Project.toDto(): ProjectDto = ProjectDto(
    id = id.value,
    name = name,
    screens = screens.values.map { it.toDto() },
    startScreenId = startScreenId?.value,
    theme = theme.toDto(),
)

fun ProjectDto.toDomain(): Project {
    val screenList = screens.map { it.toDomain() }
    return Project(
        id = ProjectId(id),
        name = name,
        screens = screenList.associateBy { it.id },
        startScreenId = startScreenId?.let { ScreenId(it) },
        theme = theme.toDomain(),
    )
}

fun ThemeSpec.toDto(): ThemeSpecDto = ThemeSpecDto(
    seedColor = seedColor,
    isDark = isDark,
    contrastLevel = contrastLevel,
    style = style,
)

fun ThemeSpecDto.toDomain(): ThemeSpec = ThemeSpec(
    seedColor = seedColor,
    isDark = isDark,
    contrastLevel = contrastLevel,
    style = style,
)

fun Screen.toDto(): ScreenDto = ScreenDto(
    id = id.value,
    name = name,
    root = root.toDto(),
)

fun ScreenDto.toDomain(): Screen = Screen(
    id = ScreenId(id),
    name = name,
    root = root.toDomain(),
)