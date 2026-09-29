package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.SlotId

/**
 * Renders a single component type into Kotlin source.
 *
 * One emitter per [io.github.chandu4221.designode.domain.model.ComponentTypeId].
 * The generator recursively renders children first and passes them in as
 * already-formatted strings, so emitters never need to know about the tree.
 */
interface CodeEmitter {

    /**
     * @param node     the node being emitted (properties, variant, modifiers)
     * @param children already-rendered child source strings, keyed by slot
     * @param indent   the indentation prefix for this node's line(s)
     * @return Kotlin source for this node, including the given indent
     */
    fun emit(
        node: AtomicNode,
        children: Map<SlotId, List<String>>,
        indent: String,
    ): String
}