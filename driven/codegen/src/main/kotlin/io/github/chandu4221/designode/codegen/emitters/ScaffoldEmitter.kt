package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.codegen.formatSlotLambda
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class ScaffoldEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val inner = "$indent    "
        val contentBody = children.slot("content")

        val args = buildList {
            children.slot("topBar").takeIf { it.isNotEmpty() }?.let {
                add(formatSlotLambda("topBar", it, inner))
            }
            children.slot("bottomBar").takeIf { it.isNotEmpty() }?.let {
                add(formatSlotLambda("bottomBar", it, inner))
            }
            children.slot("snackbarHost").takeIf { it.isNotEmpty() }?.let {
                add(formatSlotLambda("snackbarHost", it, inner))
            }
            children.slot("floatingActionButton").takeIf { it.isNotEmpty() }?.let {
                add(formatSlotLambda("floatingActionButton", it, inner))
            }
            add(formatSlotLambda("content", contentBody, inner, parameter = "padding"))
            if (node.modifiers.isNotEmpty()) {
                add("${inner}modifier = ${formatModifierChain(node.modifiers)},")
            }
        }

        return buildString {
            append("${indent}Scaffold(\n")
            args.forEach { append(it).append("\n") }
            append("$indent)")
        }
    }

    private fun Map<SlotId, List<String>>.slot(name: String): String =
        this[SlotId(name)].orEmpty().joinToString("\n")

    /**
     * Overload of the shared [formatSlotLambda] that adds a lambda parameter.
     * Content is `content = { padding -> ... }`.
     */
    private fun formatSlotLambda(
        name: String,
        body: String,
        indent: String,
        parameter: String,
    ): String {
        if (body.isEmpty()) {
            return "${indent}${name} = { _ -> },"
        }
        val indented = body.lines().joinToString("\n") { line ->
            if (line.isBlank()) line else "    $line"
        }
        return buildString {
            append("${indent}$name = { $parameter ->\n")
            append(indented).append("\n")
            append("${indent}},")
        }
    }
}