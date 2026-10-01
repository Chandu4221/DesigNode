package io.github.chandu4221.designode.domain.model

object MaterialColorRoles {
    const val PRIMARY = "primary"
    const val ON_PRIMARY = "onPrimary"
    const val PRIMARY_CONTAINER = "primaryContainer"
    const val ON_PRIMARY_CONTAINER = "onPrimaryContainer"

    const val SECONDARY = "secondary"
    const val ON_SECONDARY = "onSecondary"
    const val SECONDARY_CONTAINER = "secondaryContainer"
    const val ON_SECONDARY_CONTAINER = "onSecondaryContainer"

    const val TERTIARY = "tertiary"
    const val ON_TERTIARY = "onTertiary"
    const val TERTIARY_CONTAINER = "tertiaryContainer"
    const val ON_TERTIARY_CONTAINER = "onTertiaryContainer"

    const val ERROR = "error"
    const val ON_ERROR = "onError"
    const val ERROR_CONTAINER = "errorContainer"
    const val ON_ERROR_CONTAINER = "onErrorContainer"

    const val BACKGROUND = "background"
    const val ON_BACKGROUND = "onBackground"

    const val SURFACE = "surface"
    const val ON_SURFACE = "onSurface"
    const val SURFACE_VARIANT = "surfaceVariant"
    const val ON_SURFACE_VARIANT = "onSurfaceVariant"

    const val OUTLINE = "outline"
    const val OUTLINE_VARIANT = "outlineVariant"

    val ALL: List<String> = listOf(
        PRIMARY, ON_PRIMARY, PRIMARY_CONTAINER, ON_PRIMARY_CONTAINER,
        SECONDARY, ON_SECONDARY, SECONDARY_CONTAINER, ON_SECONDARY_CONTAINER,
        TERTIARY, ON_TERTIARY, TERTIARY_CONTAINER, ON_TERTIARY_CONTAINER,
        ERROR, ON_ERROR, ERROR_CONTAINER, ON_ERROR_CONTAINER,
        BACKGROUND, ON_BACKGROUND,
        SURFACE, ON_SURFACE, SURFACE_VARIANT, ON_SURFACE_VARIANT,
        OUTLINE, OUTLINE_VARIANT,
    )
}
