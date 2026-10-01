package io.github.chandu4221.designode.ui

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId

data class EditorState(
    val root: AtomicNode,
    val projectName: String = "Untitled",
    val selectedId: NodeId? = null,
    val hoveredId: NodeId? = null,
    val dragType: ComponentTypeId? = null,
    val dropTargetId: NodeId? = null,
    val exportDialogOpen: Boolean = false,
    val projectPickerOpen: Boolean = false,
    val saveStatus: SaveStatus = SaveStatus.Idle,
)

sealed interface SaveStatus {
    data object Idle : SaveStatus
    data object Saving : SaveStatus
    data object Saved : SaveStatus
    data class Failed(val message: String) : SaveStatus
}