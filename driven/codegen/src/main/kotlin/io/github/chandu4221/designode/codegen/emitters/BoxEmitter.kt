package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.ALIGNMENTS
import io.github.chandu4221.designode.codegen.Properties
import io.github.chandu4221.designode.codegen.formatBlock
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class BoxEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val p = Properties(node)
        val args = buildList {
            ALIGNMENTS[p.enumValue("contentAlignment", "topStart")]?.let {
                add("contentAlignment = $it")
            }
            if (node.modifiers.isNotEmpty()) {
                add("modifier = ${formatModifierChain(node.modifiers)}")
            }
        }
        val body = children[SlotId("content")].orEmpty().joinToString("\n")
        return formatBlock("Box", args, body, indent)
    }
}