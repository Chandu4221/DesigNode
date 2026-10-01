package io.github.chandu4221.designode.ui

import androidx.compose.ui.geometry.Offset
import io.github.chandu4221.designode.catalog.Material3Catalog
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.VariantId
import io.github.chandu4221.designode.domain.port.NodeEventPublisher
import io.github.chandu4221.designode.domain.port.RandomNodeIdGenerator
import io.github.chandu4221.designode.domain.service.NodeTree
import io.github.chandu4221.designode.domain.service.SlotValidator
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.ui.canvas.HitTestRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditorViewModel {

    private val registry = Material3Catalog.registry()
    private val validator = SlotValidator(registry)
    private val publisher = NodeEventPublisher { /* no subscribers yet */ }
    private val idGenerator = RandomNodeIdGenerator

    /** Owned here so the drag-drop logic can hit-test against live layout. */
    val hitTestRegistry = HitTestRegistry()

    val specs: List<ComponentSpec> = registry.all()

    fun spec(type: ComponentTypeId): ComponentSpec? = registry.spec(type)

    private val tree = NodeTree(
        registry = registry,
        validator = validator,
        publisher = publisher,
        idGenerator = idGenerator,
        root = AtomicNode(
            id = idGenerator.next(),
            type = ComponentTypeId("Column"),
        ),
    )

    private val _state = MutableStateFlow(EditorState(root = tree.root))
    val state: StateFlow<EditorState> = _state.asStateFlow()

    // ─────────────────────────────────────────────────────
    // Selection
    // ─────────────────────────────────────────────────────

    fun selectNode(id: NodeId?) {
        _state.update { it.copy(selectedId = id) }
    }

    fun hoverNode(id: NodeId?) {
        _state.update { it.copy(hoveredId = id) }
    }

    // ─────────────────────────────────────────────────────
    // Edit intents
    // ─────────────────────────────────────────────────────

    fun updateVariant(id: NodeId, variant: VariantId?) {
        tree.updateVariant(id, variant)
        syncRoot()
    }

    fun updateProperty(id: NodeId, key: PropertyKey, value: Value?) {
        tree.updateProperty(id, key, value)
        syncRoot()
    }

    fun removeNode(id: NodeId) {
        tree.remove(id).onSuccess {
            _state.update { it.copy(selectedId = null) }
            syncRoot()
        }
    }

    // ─────────────────────────────────────────────────────
    // Drag-and-drop
    // ─────────────────────────────────────────────────────

    fun beginDrag(type: ComponentTypeId) {
        _state.update { it.copy(dragType = type, dropTargetId = null) }
    }

    fun updateDragPosition(positionInRoot: Offset) {
        val target = computeDropTarget(positionInRoot)
        _state.update { it.copy(dropTargetId = target) }
    }

    fun commitDrag() {
        val current = _state.value
        val draggedType = current.dragType
        val targetId = current.dropTargetId
        _state.update { it.copy(dragType = null, dropTargetId = null) }

        if (draggedType == null || targetId == null) return

        val targetNode = tree.find(targetId) ?: return
        val targetSpec = registry.spec(targetNode.type) ?: return
        val slot = targetSpec.slots.firstOrNull { it.isDefault }
            ?: targetSpec.slots.singleOrNull()
            ?: return

        tree.insert(targetNode.id, slot.id, draggedType).onSuccess { newId ->
            _state.update { it.copy(selectedId = newId) }
            syncRoot()
        }
    }

    fun cancelDrag() {
        _state.update { it.copy(dragType = null, dropTargetId = null) }
    }

    private fun computeDropTarget(positionInRoot: Offset): NodeId? {
        val hitId = hitTestRegistry.hitTest(positionInRoot.x, positionInRoot.y) ?: return null
        // Guard against stale bounds for nodes that no longer exist.
        val node = tree.find(hitId) ?: return null
        // Reject targets with no usable slot.
        val spec = registry.spec(node.type) ?: return null
        val hasSlot = spec.slots.any { it.isDefault } || spec.slots.size == 1
        return if (hasSlot) node.id else null
    }

    private fun syncRoot() {
        _state.update { it.copy(root = tree.root) }
    }
}