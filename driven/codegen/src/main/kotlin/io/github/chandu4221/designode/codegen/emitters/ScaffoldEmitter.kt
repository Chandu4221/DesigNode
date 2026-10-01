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
            add(formatContentSlot("content", contentBody, inner))
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
     * Scaffold's content slot receives a PaddingValues that consumers are
     * expected to apply — otherwise content renders under the top bar and
     * FAB. We apply the padding directly to the content's root composable
     * so there is no redundant wrapper Box and layout remains edge-to-edge friendly.
     */
    private fun formatContentSlot(name: String, body: String, indent: String): String {
        if (body.isEmpty()) {
            return "${indent}$name = { _ -> },"
        }
        val innerIndent = "$indent    "
        val appliedBody = applyPaddingToRoot(body, innerIndent)
        return buildString {
            append("${indent}$name = { padding ->\n")
            append(appliedBody).append("\n")
            append("${indent}},")
        }
    }

    private fun applyPaddingToRoot(body: String, indent: String): String {
        // If content root already has a modifier chain, prepend .padding(padding)
        if (body.contains("modifier = Modifier")) {
            return body.replaceFirst("modifier = Modifier", "modifier = Modifier.padding(padding)")
        }

        // If content root has parameter parentheses, insert modifier argument as the first parameter
        val parenIndex = body.indexOf("(\n")
        if (parenIndex != -1) {
            val before = body.substring(0, parenIndex + 2)
            val after = body.substring(parenIndex + 2)
            return before + "${indent}modifier = Modifier.padding(padding),\n" + after
        }

        // If content root has a trailing lambda without parentheses (e.g. Column { ... })
        val lambdaIndex = body.indexOf(" {")
        if (lambdaIndex != -1) {
            val name = body.substring(0, lambdaIndex)
            val rest = body.substring(lambdaIndex)
            val baseIndent = body.takeWhile { it.isWhitespace() }
            return "$name(\n${indent}modifier = Modifier.padding(padding),\n$baseIndent)$rest"
        }

        // If content root has empty parentheses without body (e.g. Spacer())
        val emptyParenIndex = body.indexOf("()")
        if (emptyParenIndex != -1) {
            val name = body.substring(0, emptyParenIndex)
            val baseIndent = body.takeWhile { it.isWhitespace() }
            return "$name(\n${indent}modifier = Modifier.padding(padding),\n$baseIndent)"
        }

        return body
    }
}