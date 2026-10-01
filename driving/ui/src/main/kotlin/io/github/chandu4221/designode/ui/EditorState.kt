package io.github.chandu4221.designode.ui

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId

data class EditorState(
    val root: AtomicNode,
    val selectedId: NodeId? = null,
    val hoveredId: NodeId? = null,
    val dragType: ComponentTypeId? = null,
    val dropTargetId: NodeId? = null,
    val exportDialogOpen: Boolean = false,
)