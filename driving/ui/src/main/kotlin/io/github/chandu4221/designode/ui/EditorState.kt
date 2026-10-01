package io.github.chandu4221.designode.ui

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.NodeId

/**
 * The complete UI state for the editor.
 *
 * Immutable snapshot. Updated by [EditorViewModel] in response to intents.
 * The UI collects this via `collectAsState()`.
 */
data class EditorState(
    val root: AtomicNode,
    val selectedId: NodeId? = null,
    val hoveredId: NodeId? = null,
)