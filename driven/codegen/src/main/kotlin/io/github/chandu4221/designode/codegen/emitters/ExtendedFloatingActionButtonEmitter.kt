package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.Properties
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.codegen.formatSlotLambda
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class ExtendedFloatingActionButtonEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val p = Properties(node)
        val inner = "$indent    "
        val expanded = p.bool("expanded", true)

        val args = buildList {
            add("${inner}onClick = { /* TODO */ },")
            add(formatSlotLambda("icon", children.slot("icon"), inner))
            if (expanded) {
                add(formatSlotLambda("text", children.slot("text"), inner))
            }
            if (node.modifiers.isNotEmpty()) {
                add("${inner}modifier = ${formatModifierChain(node.modifiers)},")
            }
        }

        return buildString {
            append("${indent}ExtendedFloatingActionButton(\n")
            args.forEach { append(it).append("\n") }
            append("$indent)")
        }
    }

    private fun Map<SlotId, List<String>>.slot(name: String): String =
        this[SlotId(name)].orEmpty().joinToString("\n")
}