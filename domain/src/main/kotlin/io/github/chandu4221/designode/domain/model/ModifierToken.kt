package io.github.chandu4221.designode.domain.model

sealed interface ModifierToken {
    val allowedScopes: Set<LayoutScope>

    data class Padding(val all: Float) : ModifierToken {
        override val allowedScopes = setOf(LayoutScope.Unscoped)
    }

    data class Size(val width: Float?, val height: Float?) : ModifierToken {
        override val allowedScopes = setOf(LayoutScope.Unscoped)
    }

    data class FillMaxWidth(val fraction: Float = 1f) : ModifierToken {
        override val allowedScopes = setOf(LayoutScope.Unscoped)
    }

    data class FillMaxHeight(val fraction: Float = 1f) : ModifierToken {
        override val allowedScopes = setOf(LayoutScope.Unscoped)
    }

    data class Weight(val value: Float) : ModifierToken {
        override val allowedScopes = setOf(LayoutScope.RowScope, LayoutScope.ColumnScope)
    }
}