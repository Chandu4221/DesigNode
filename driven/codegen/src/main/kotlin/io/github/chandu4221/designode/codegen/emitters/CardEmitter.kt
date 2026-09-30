package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.formatBlock
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.VariantId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class CardEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val composable = composableFor(node.variant)

        val args = buildList {
            if (node.modifiers.isNotEmpty()) {
                add("modifier = ${formatModifierChain(node.modifiers)}")
            }
        }

        val body = children[SlotId("content")].orEmpty().joinToString("\n")
        return formatBlock(composable, args, body, indent)
    }

    private fun composableFor(variant: VariantId?): String = when (variant?.value) {
        "elevated" -> "ElevatedCard"
        "outlined" -> "OutlinedCard"
        else -> "Card"  // null or "filled"
    }
}