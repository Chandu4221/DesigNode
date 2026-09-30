package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.ALIGNMENTS
import io.github.chandu4221.designode.codegen.ARRANGEMENTS
import io.github.chandu4221.designode.codegen.Properties
import io.github.chandu4221.designode.codegen.formatBlock
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class RowEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val p = Properties(node)
        val args = buildList {
            ARRANGEMENTS[p.enumValue("horizontalArrangement", "start")]?.let {
                add("horizontalArrangement = $it")
            }
            ALIGNMENTS[p.enumValue("verticalAlignment", "top")]?.let {
                add("verticalAlignment = $it")
            }
            if (node.modifiers.isNotEmpty()) {
                add("modifier = ${formatModifierChain(node.modifiers)}")
            }
        }
        val body = children[SlotId("content")].orEmpty().joinToString("\n")
        return formatBlock("Row", args, body, indent)
    }
}