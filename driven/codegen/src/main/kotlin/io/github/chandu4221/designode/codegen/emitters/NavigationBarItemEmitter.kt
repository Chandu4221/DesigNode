package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.formatCall
import io.github.chandu4221.designode.codegen.formatSlotLambda
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class NavigationBarItemEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val inner = "$indent    "

        val args = buildList {
            add("${inner}selected = false,")
            add("${inner}onClick = { /* TODO */ },")
            add(formatSlotLambda("icon", children.slot("icon"), inner))
            add(formatSlotLambda("label", children.slot("label"), inner))
        }

        return buildString {
            append("${indent}NavigationBarItem(\n")
            args.forEach { append(it).append("\n") }
            append("$indent)")
        }
    }

    private fun Map<SlotId, List<String>>.slot(name: String): String =
        this[SlotId(name)].orEmpty().joinToString("\n")
}