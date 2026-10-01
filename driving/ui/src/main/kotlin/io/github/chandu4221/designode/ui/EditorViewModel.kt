package io.github.chandu4221.designode.ui

import androidx.compose.ui.geometry.Offset
import io.github.chandu4221.designode.catalog.Material3Catalog
import io.github.chandu4221.designode.codegen.ComposeSourceGenerator
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
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
    private val sourceGenerator = ComposeSourceGenerator()

    val hitTestRegistry = HitTestRegistry()

    val specs: List<ComponentSpec> = registry.all()

    fun spec(type: ComponentTypeId): ComponentSpec? = registry.spec(type)

    private val tree = NodeTree(
        registry = registry,
        validator = validator,
        publisher = publisher,
        idGenerator = idGenerator,
        root = defaultScreen(),
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
        val current = _state.value
        val draggedType = current.dragType ?: return
        val target = computeValidDropTarget(positionInRoot, draggedType)
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
        val slotId = resolveDropSlot(targetSpec, draggedType) ?: return

        tree.insert(targetNode.id, slotId, draggedType).onSuccess { newId ->
            _state.update { it.copy(selectedId = newId) }
            syncRoot()
        }
    }

    fun cancelDrag() {
        _state.update { it.copy(dragType = null, dropTargetId = null) }
    }

    // ─────────────────────────────────────────────────────
    // Export
    // ─────────────────────────────────────────────────────

    fun openExportDialog() {
        _state.update { it.copy(exportDialogOpen = true) }
    }

    fun closeExportDialog() {
        _state.update { it.copy(exportDialogOpen = false) }
    }

    fun generateCode(): String = sourceGenerator.generate(tree.root)

    // ─────────────────────────────────────────────────────
    // Internals
    // ─────────────────────────────────────────────────────

    private fun computeValidDropTarget(
        positionInRoot: Offset,
        draggedType: ComponentTypeId,
    ): NodeId? {
        val hitId = hitTestRegistry.hitTest(positionInRoot.x, positionInRoot.y) ?: return null
        val candidate = findContainerAncestor(hitId) ?: return null

        val node = tree.find(candidate) ?: return null
        val spec = registry.spec(node.type) ?: return null
        val slotId = resolveDropSlot(spec, draggedType) ?: return null

        val fakeChild = AtomicNode(
            id = NodeId("__drag-preview__"),
            type = draggedType,
        )
        return if (validator.canDrop(node, slotId, fakeChild).isSuccess) candidate else null
    }

    private fun findContainerAncestor(startId: NodeId): NodeId? {
        var current = tree.find(startId)
        while (current != null) {
            val spec = registry.spec(current.type)
            if (spec != null && spec.slots.isNotEmpty()) return current.id
            current = tree.parentOf(current.id)
        }
        return null
    }

    private fun resolveDropSlot(
        targetSpec: ComponentSpec,
        draggedType: ComponentTypeId,
    ): SlotId? {
        val explicit = targetSpec.slots.filter { draggedType in it.accepts }
        if (explicit.isNotEmpty()) {
            val preferred = explicit.firstOrNull { it.isDefault } ?: explicit.first()
            return preferred.id
        }
        val default = targetSpec.slots.firstOrNull { it.isDefault }
        if (default != null) return default.id
        return targetSpec.slots.singleOrNull()?.id
    }

    private fun syncRoot() {
        _state.update { it.copy(root = tree.root) }
    }

    private fun defaultScreen(): AtomicNode {
        val contentColumn = AtomicNode(
            id = idGenerator.next(),
            type = ComponentTypeId("Column"),
        )
        return AtomicNode(
            id = idGenerator.next(),
            type = ComponentTypeId("Scaffold"),
            slots = mapOf(
                SlotId("content") to SlotContent.One(contentColumn),
            ),
        )
    }
}