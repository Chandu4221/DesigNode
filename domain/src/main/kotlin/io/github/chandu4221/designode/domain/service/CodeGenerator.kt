package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.nodes
import io.github.chandu4221.designode.domain.port.CodeEmitter

/**
 * Walks an [AtomicNode] tree and dispatches to [CodeEmitter]s by component type.
 *
 * The generator knows nothing about Kotlin syntax. Every component-specific
 * decision (which composable, how to pass properties, how to nest children)
 * lives in the emitter for that component type.
 */
class CodeGenerator(
    private val emitters: Map<ComponentTypeId, CodeEmitter>,
) {

    /**
     * Renders [root] and all its descendants into a Kotlin source string.
     * Throws if any node in the tree has no registered emitter.
     */
    fun generate(root: AtomicNode): String = render(root, "")

    /**
     * Returns true if every component type reachable from [root] has an emitter.
     * Useful for validating a tree before attempting to generate code.
     */
    fun canGenerate(root: AtomicNode): Boolean =
        emitters.containsKey(root.type) &&
                root.slots.values.all { content ->
                    content.nodes().all { canGenerate(it) }
                }

    private fun render(node: AtomicNode, indent: String): String {
        val emitter = emitters[node.type]
            ?: error("No emitter registered for component type: ${node.type.value}")

        val renderedChildren: Map<SlotId, List<String>> =
            node.slots.mapValues { (_, content) ->
                content.nodes().map { child -> render(child, "$indent    ") }
            }

        return emitter.emit(node, renderedChildren, indent)
    }
}