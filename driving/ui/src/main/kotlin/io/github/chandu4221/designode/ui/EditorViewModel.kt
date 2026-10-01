package io.github.chandu4221.designode.ui

import io.github.chandu4221.designode.catalog.Material3Catalog
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.port.NodeEventPublisher
import io.github.chandu4221.designode.domain.port.RandomNodeIdGenerator
import io.github.chandu4221.designode.domain.service.NodeTree
import io.github.chandu4221.designode.domain.service.SlotValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single state holder for the editor.
 *
 * Owns the tree and exposes a [StateFlow] of [EditorState]. UI components
 * collect state and dispatch intents through public methods. Nothing
 * mutates the tree directly except this class.
 */
class EditorViewModel {

    private val registry = Material3Catalog.registry()
    private val validator = SlotValidator(registry)
    private val publisher = NodeEventPublisher { /* no subscribers yet */ }
    private val idGenerator = RandomNodeIdGenerator

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
    // Intents
    // ─────────────────────────────────────────────────────

    fun selectNode(id: NodeId?) {
        _state.update { it.copy(selectedId = id) }
    }

    fun hoverNode(id: NodeId?) {
        _state.update { it.copy(hoveredId = id) }
    }
}