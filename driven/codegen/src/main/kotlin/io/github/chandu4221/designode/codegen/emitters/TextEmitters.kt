package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.Properties
import io.github.chandu4221.designode.codegen.asColorLiteral
import io.github.chandu4221.designode.codegen.escapeForStringLiteral
import io.github.chandu4221.designode.codegen.formatCall
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class TextEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val p = Properties(node)
        val args = buildList {
            add("text = \"${p.text("text").escapeForStringLiteral()}\"")
            if (node.modifiers.isNotEmpty()) {
                add("modifier = ${formatModifierChain(node.modifiers)}")
            }
            add("style = MaterialTheme.typography.${p.enumValue("style", "bodyMedium")}")
            add("color = ${p.colorExpression("color")}")
            add("textAlign = ${p.enumValue("textAlign", "start").toTextAlign()}")
        }
        return formatCall("Text", args, indent)
    }

    private fun String.toTextAlign(): String = when (this) {
        "start" -> "TextAlign.Start"
        "center" -> "TextAlign.Center"
        "end" -> "TextAlign.End"
        "justify" -> "TextAlign.Justify"
        else -> "TextAlign.Start"
    }
}