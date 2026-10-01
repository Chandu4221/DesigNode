package io.github.chandu4221.designode.ui

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditorViewModel {

    private val registry = Material3Catalog.registry()
    private val validator = SlotValidator(registry)
    private val publisher = NodeEventPublisher { /* no subscribers yet */ }
    private val idGenerator = RandomNodeIdGenerator

    /** All component specs, for the palette to render. */
    val specs: List<ComponentSpec> = registry.all()

    /** Look up the spec for a given type. Null if unknown. */
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
    // Intents
    // ─────────────────────────────────────────────────────

    fun selectNode(id: NodeId?) {
        _state.update { it.copy(selectedId = id) }
    }

    fun hoverNode(id: NodeId?) {
        _state.update { it.copy(hoveredId = id) }
    }

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

    private fun syncRoot() {
        _state.update { it.copy(root = tree.root) }
    }
}