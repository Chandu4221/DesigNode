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

internal fun formatCall(name: String, args: List<String>, indent: String): String {
    if (args.isEmpty()) return "$indent$name()"
    return buildString {
        append("${indent}$name(\n")
        args.forEach { append("${indent}    $it,\n") }
        append("$indent)")
    }
}

internal fun formatBlock(
    name: String,
    args: List<String>,
    body: String,
    indent: String,
): String = buildString {
    append("$indent$name")
    if (args.isNotEmpty()) {
        append("(\n")
        args.forEach { append("${indent}    $it,\n") }
        append("$indent)")
    }
    append(" {")
    if (body.isEmpty()) {
        append("}")
    } else {
        append("\n").append(body).append("\n$indent}")
    }
}

/**
 * Renders a named slot lambda inside a parameter list:
 *
 *     topBar = {
 *         <body, indented one extra level>
 *     },
 *
 * If [body] is empty, emits `name = {},` on a single line.
 */
internal fun formatSlotLambda(name: String, body: String, indent: String): String {
    if (body.isEmpty()) return "${indent}${name} = {},"
    val indented = indentLines(body, "    ")
    return buildString {
        append("${indent}$name = {\n")
        append(indented).append("\n")
        append("${indent}},")
    }
}

/**
 * Prefixes every non-blank line of [text] with [prefix].
 */
internal fun indentLines(text: String, prefix: String): String =
    text.lines().joinToString("\n") { line ->
        if (line.isBlank()) line else "$prefix$line"
    }

internal val ARRANGEMENTS = mapOf(
    "start" to "Arrangement.Start",
    "end" to "Arrangement.End",
    "top" to "Arrangement.Top",
    "bottom" to "Arrangement.Bottom",
    "center" to "Arrangement.Center",
    "spaceBetween" to "Arrangement.SpaceBetween",
    "spaceAround" to "Arrangement.SpaceAround",
    "spaceEvenly" to "Arrangement.SpaceEvenly",
)

internal val ALIGNMENTS = mapOf(
    "top" to "Alignment.Top",
    "bottom" to "Alignment.Bottom",
    "start" to "Alignment.Start",
    "end" to "Alignment.End",
    "centerVertically" to "Alignment.CenterVertically",
    "centerHorizontally" to "Alignment.CenterHorizontally",
    "topStart" to "Alignment.TopStart",
    "topCenter" to "Alignment.TopCenter",
    "topEnd" to "Alignment.TopEnd",
    "centerStart" to "Alignment.CenterStart",
    "center" to "Alignment.Center",
    "centerEnd" to "Alignment.CenterEnd",
    "bottomStart" to "Alignment.BottomStart",
    "bottomCenter" to "Alignment.BottomCenter",
    "bottomEnd" to "Alignment.BottomEnd",
)