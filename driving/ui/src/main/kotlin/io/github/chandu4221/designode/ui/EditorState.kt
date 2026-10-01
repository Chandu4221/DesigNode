package io.github.chandu4221.designode.ui

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.SlotId

import io.github.chandu4221.designode.domain.model.ThemeSpec

/** Represents a resolved drop target node and its specific slot. */
data class DropTarget(
    val nodeId: NodeId,
    val slotId: SlotId,
)

data class EditorState(
    val root: AtomicNode,
    val projectName: String = "Untitled",
    val selectedId: NodeId? = null,
    val hoveredId: NodeId? = null,
    val dragType: ComponentTypeId? = null,
    val dropTarget: DropTarget? = null,
    val exportDialogOpen: Boolean = false,
    val projectPickerOpen: Boolean = false,
    val themeDialogOpen: Boolean = false,
    val theme: ThemeSpec = ThemeSpec.Default,
    val saveStatus: SaveStatus = SaveStatus.Idle,
) {
    val dropTargetId: NodeId? get() = dropTarget?.nodeId
}

sealed interface SaveStatus {
    data object Idle : SaveStatus
    data object Saving : SaveStatus
    data object Saved : SaveStatus
    data class Failed(val message: String) : SaveStatus
}