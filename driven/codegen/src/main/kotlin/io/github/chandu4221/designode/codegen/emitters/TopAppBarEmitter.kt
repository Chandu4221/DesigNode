package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.codegen.formatSlotLambda
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.VariantId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class TopAppBarEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val composable = composableFor(node.variant)
        val inner = "$indent    "

        val args = buildList {
            add(formatSlotLambda("title", children.slot("title"), inner))
            add(formatSlotLambda("navigationIcon", children.slot("navigationIcon"), inner))
            add(formatSlotLambda("actions", children.slot("actions"), inner))
            if (node.modifiers.isNotEmpty()) {
                add("${inner}modifier = ${formatModifierChain(node.modifiers)},")
            }
        }

        return buildString {
            append("${indent}$composable(\n")
            args.forEach { append(it).append("\n") }
            append("$indent)")
        }
    }

    private fun Map<SlotId, List<String>>.slot(name: String): String =
        this[SlotId(name)].orEmpty().joinToString("\n")

    private fun composableFor(variant: VariantId?): String = when (variant?.value) {
        "centerAligned" -> "CenterAlignedTopAppBar"
        "medium" -> "MediumTopAppBar"
        "large" -> "LargeTopAppBar"
        else -> "TopAppBar"  // null or "small"
    }
}