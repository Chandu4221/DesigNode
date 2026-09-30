package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.formatBlock
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class NavigationBarEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val args = buildList {
            if (node.modifiers.isNotEmpty()) {
                add("modifier = ${formatModifierChain(node.modifiers)}")
            }
        }
        val body = children[SlotId("items")].orEmpty().joinToString("\n")
        return formatBlock("NavigationBar", args, body, indent)
    }
}