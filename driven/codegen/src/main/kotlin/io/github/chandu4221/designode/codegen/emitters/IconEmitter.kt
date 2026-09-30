package io.github.chandu4221.designode.codegen.emitters

import io.github.chandu4221.designode.codegen.Properties
import io.github.chandu4221.designode.codegen.asColorLiteral
import io.github.chandu4221.designode.codegen.escapeForStringLiteral
import io.github.chandu4221.designode.codegen.formatCall
import io.github.chandu4221.designode.codegen.formatModifierChain
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.port.CodeEmitter

class IconEmitter : CodeEmitter {

    override fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String {
        val p = Properties(node)
        val args = buildList {
            add("imageVector = Icons.Default.${p.text("name", "Favorite")}")
            add("contentDescription = \"${p.text("contentDescription").escapeForStringLiteral()}\"")
            if (node.modifiers.isNotEmpty()) {
                add("modifier = ${formatModifierChain(node.modifiers)}")
            } else {
                add("modifier = Modifier.size(${p.dp("size", 24f)}f.dp)")
            }
            add("tint = ${p.color("tint").asColorLiteral()}")
        }
        return formatCall("Icon", args, indent)
    }
}