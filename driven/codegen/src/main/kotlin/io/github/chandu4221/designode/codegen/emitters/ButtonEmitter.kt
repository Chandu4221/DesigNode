package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.Properties
import io.github.chandu4221.designode.codegen.formatBlock
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.VariantId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class ButtonEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val p = Properties(node)
        val composable = composableFor(node.variant)

        val args = buildList {
            add("onClick = { /* TODO */ }")
            if (node.modifiers.isNotEmpty()) {
                add("modifier = ${formatModifierChain(node.modifiers)}")
            }
            if (!p.bool("enabled", true)) {
                add("enabled = false")
            }
        }

        val body = children[SlotId("content")].orEmpty().joinToString("\n")
        return formatBlock(composable, args, body, indent)
    }

    private fun composableFor(variant: VariantId?): String = when (variant?.value) {
        "outlined" -> "OutlinedButton"
        "text" -> "TextButton"
        "elevated" -> "ElevatedButton"
        "tonal" -> "FilledTonalButton"
        else -> "Button"  // null or "filled"
    }
}