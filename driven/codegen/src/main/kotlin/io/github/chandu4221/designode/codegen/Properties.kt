package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.Value

/**
 * Typed accessor over a node's property map.
 *
 * Returns the default when a property is absent or has the wrong type, so
 * emitters never null-check or cast.
 */
class Properties(node: AtomicNode) {

    private val map = node.properties

    fun text(key: String, default: String = ""): String =
        (map[PropertyKey(key)] as? Value.Text)?.value ?: default

    fun bool(key: String, default: Boolean = false): Boolean =
        (map[PropertyKey(key)] as? Value.Bool)?.value ?: default

    fun int(key: String, default: Int = 0): Int =
        (map[PropertyKey(key)] as? Value.IntValue)?.value ?: default

    fun dp(key: String, default: Float = 0f): Float =
        (map[PropertyKey(key)] as? Value.Dp)?.value ?: default

    fun color(key: String, default: Long = 0xFF000000L): Long =
        (map[PropertyKey(key)] as? Value.Color)?.value ?: default

    fun enumValue(key: String, default: String = ""): String =
        (map[PropertyKey(key)] as? Value.EnumValue)?.name ?: default
}