package io.github.chandu4221.designode.ui.canvas

import androidx.compose.runtime.Composable
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.SlotId

/**
 * Renders a single node in the canvas preview. One per component type,
 * keyed in the map held by [NodePreview].
 */
typealias PreviewRenderer = @Composable (node: AtomicNode, slots: SlotRenderer) -> Unit

/**
 * Renders a child node at [SlotId] recursively. Supplied by [NodePreview]
 * so renderers don't need to know about the tree, the registry, or the
 * hit-testing layer.
 */
typealias SlotRenderer = @Composable (slotId: SlotId) -> Unit

/** A map of component type to its preview renderer. */
class PreviewRenderers(
    private val renderers: Map<ComponentTypeId, PreviewRenderer>,
) {
    operator fun get(type: ComponentTypeId): PreviewRenderer? = renderers[type]
}