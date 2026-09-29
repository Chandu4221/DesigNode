package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.SlotId

/**
 * Read-only view of a node tree.
 *
 * Consumers that only inspect the tree (canvas rendering, inspector,
 * selection hit-testing) depend on this interface, never on [TreeEditor].
 */
interface TreeQuery {

    /** The current root. Always present. */
    val root: AtomicNode

    /** Finds a node anywhere in the tree by ID, or null if absent. */
    fun find(id: NodeId): AtomicNode?

    /** Finds the parent of [id], or null if [id] is root or absent. */
    fun parentOf(id: NodeId): AtomicNode?

    /** Finds which slot of [parentId] holds [childId], or null if not found. */
    fun slotOf(parentId: NodeId, childId: NodeId): SlotId?
}