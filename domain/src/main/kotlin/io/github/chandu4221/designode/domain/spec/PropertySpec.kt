package io.github.chandu4221.designode.domain.spec

import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.PropertyKind
import io.github.chandu4221.designode.domain.model.Value

data class PropertySpec(
    val key: PropertyKey,
    val kind: PropertyKind,
    val label: String,
    val default: Value? = null,
    val required: Boolean = false,
    val visibility: Visibility = Visibility.Always,
    val enumOptions: List<String> = emptyList(),
)