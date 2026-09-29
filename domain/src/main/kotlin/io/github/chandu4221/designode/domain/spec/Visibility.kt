package io.github.chandu4221.designode.domain.spec

import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.VariantId

sealed interface Visibility {
    data object Always : Visibility
    data class WhenVariant(val variants: Set<VariantId>) : Visibility
    data class WhenProperty(val key: PropertyKey, val value: Value) : Visibility
}