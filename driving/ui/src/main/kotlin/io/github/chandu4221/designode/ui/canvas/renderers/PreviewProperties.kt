package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.nodes
import io.github.chandu4221.designode.ui.theme.resolveColorRole

internal fun Map<PropertyKey, Value>.text(key: String, default: String = ""): String =
    (this[PropertyKey(key)] as? Value.Text)?.value ?: default

internal fun Map<PropertyKey, Value>.enumValue(key: String, default: String = ""): String =
    (this[PropertyKey(key)] as? Value.EnumValue)?.name ?: default

internal fun Map<PropertyKey, Value>.color(key: String, default: Long = 0xFF000000L): Long =
    (this[PropertyKey(key)] as? Value.Color)?.value ?: default

@Composable
internal fun Map<PropertyKey, Value>.composeColor(key: String, default: Long = 0xFF000000L): Color =
    when (val v = this[PropertyKey(key)]) {
        is Value.ColorRole -> resolveColorRole(v.role)
        is Value.Color -> Color(v.value)
        else -> Color(default)
    }

internal fun Map<PropertyKey, Value>.dp(key: String, default: Float = 0f): Float =
    (this[PropertyKey(key)] as? Value.Dp)?.value ?: default

internal fun Map<PropertyKey, Value>.bool(key: String, default: Boolean = false): Boolean =
    (this[PropertyKey(key)] as? Value.Bool)?.value ?: default

internal fun AtomicNode.hasChildren(slot: String): Boolean =
    slots[SlotId(slot)]?.nodes()?.isNotEmpty() == true