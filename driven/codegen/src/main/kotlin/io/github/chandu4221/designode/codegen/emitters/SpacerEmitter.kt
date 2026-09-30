package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class SpacerEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val modifier = formatModifierChain(node.modifiers)
        return "${indent}Spacer(modifier = $modifier)"
    }
}