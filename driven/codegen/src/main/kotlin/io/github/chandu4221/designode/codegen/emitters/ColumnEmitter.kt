package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.ALIGNMENTS
import io.github.chandu4221.designode.codegen.ARRANGEMENTS
import io.github.chandu4221.designode.codegen.Properties
import io.github.chandu4221.designode.codegen.formatBlock
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class ColumnEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val p = Properties(node)
        val args = buildList {
            ARRANGEMENTS[p.enumValue("verticalArrangement", "top")]?.let {
                add("verticalArrangement = $it")
            }
            ALIGNMENTS[p.enumValue("horizontalAlignment", "start")]?.let {
                add("horizontalAlignment = $it")
            }
            if (node.modifiers.isNotEmpty()) {
                add("modifier = ${formatModifierChain(node.modifiers)}")
            }
        }
        val body = children[SlotId("content")].orEmpty().joinToString("\n")
        return formatBlock("Column", args, body, indent)
    }
}