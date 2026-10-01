package io.github.chandu4221.designode.ui.canvas

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.draw.drawBehind
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.nodes

/**
 * Renders [node] and its subtree recursively. Each node's bounds are
 * captured into [hitTestRegistry] so the overlay can map clicks back to
 * node IDs.
 */
@Composable
fun NodePreview(
    node: AtomicNode,
    renderers: PreviewRenderers,
    hitTestRegistry: HitTestRegistry,
    modifier: Modifier = Modifier,
    selectedId: NodeId? = null,
    hoveredId: NodeId? = null,
) {
    val renderer = renderers[node.type]
    if (renderer == null) {
        MissingRendererPlaceholder(node)
        return
    }

    val slotRenderer: SlotRenderer = { slotId ->
        val content = node.slots[slotId] ?: SlotContent.Empty
        content.nodes().forEach { child ->
            NodePreview(
                node = child,
                renderers = renderers,
                hitTestRegistry = hitTestRegistry,
                selectedId = selectedId,
                hoveredId = hoveredId,
            )
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                hitTestRegistry.record(node.id, coordinates.boundsInRoot())
            }
            .drawSelectionOutline(
                selected = node.id == selectedId,
                hovered = node.id == hoveredId,
            )
    ) {
        renderer(node, slotRenderer)
    }
}

private fun Modifier.drawSelectionOutline(
    selected: Boolean,
    hovered: Boolean,
): Modifier = drawBehind {
    if (selected) {
        drawRect(
            color = Color(0xFF6750A4),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f),
        )
    } else if (hovered) {
        drawRect(
            color = Color(0x88_6750A4),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f),
        )
    }
}

@Composable
private fun MissingRendererPlaceholder(node: AtomicNode) {
    Box(Modifier) {
        androidx.compose.material3.Text(
            text = "No renderer for ${node.type.value}",
            color = androidx.compose.material3.MaterialTheme.colorScheme.error,
        )
    }
}