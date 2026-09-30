package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.domain.model.ModifierToken

internal fun Long.asColorLiteral(): String {
    val hex = this.toULong().toString(16).uppercase().padStart(8, '0')
    return "Color(0x${hex}L)"
}

internal fun Float.asDpLiteral(): String = "${this}f.dp"

internal fun String.escapeForStringLiteral(): String =
    replace("\\", "\\\\").replace("\"", "\\\"")

internal fun formatModifier(modifier: ModifierToken): String = when (modifier) {
    is ModifierToken.Padding -> "padding(${modifier.all}f.dp)"
    is ModifierToken.Size -> {
        val w = modifier.width?.let { "${it}f.dp" } ?: "Dp.Unspecified"
        val h = modifier.height?.let { "${it}f.dp" } ?: "Dp.Unspecified"
        "size(width = $w, height = $h)"
    }
    is ModifierToken.FillMaxWidth -> "fillMaxWidth(${modifier.fraction}f)"
    is ModifierToken.FillMaxHeight -> "fillMaxHeight(${modifier.fraction}f)"
    is ModifierToken.Weight -> "weight(${modifier.value}f)"
}

internal fun formatModifierChain(modifiers: List<ModifierToken>): String {
    if (modifiers.isEmpty()) return "Modifier"
    return "Modifier." + modifiers.joinToString(".") { formatModifier(it) }
}

/**
 * Formats a composable call with the given arguments, each on its own line.
 * Empty args produce a bare `Name()` call.
 */
internal fun formatCall(name: String, args: List<String>, indent: String): String {
    if (args.isEmpty()) return "$indent$name()"
    return buildString {
        append("${indent}$name(\n")
        args.forEach { append("${indent}    $it,\n") }
        append("$indent)")
    }
}