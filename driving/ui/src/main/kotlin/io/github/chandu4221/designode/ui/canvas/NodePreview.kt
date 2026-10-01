package io.github.chandu4221.designode.ui.canvas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.nodes

@Composable
fun NodePreview(
    node: AtomicNode,
    renderers: PreviewRenderers,
    hitTestRegistry: HitTestRegistry,
    modifier: Modifier = Modifier,
    selectedId: NodeId? = null,
    hoveredId: NodeId? = null,
    dropTargetId: NodeId? = null,
) {
    val renderer = renderers[node.type]
    if (renderer == null) {
        MissingRendererPlaceholder(node)
        return
    }

    val slotRenderer: SlotRenderer = { slotId ->
        val content = node.slots[slotId] ?: SlotContent.Empty
        val isScaffoldContent = node.type.value == "Scaffold" && slotId.value == "content"
        content.nodes().forEach { child ->
            NodePreview(
                node = child,
                renderers = renderers,
                hitTestRegistry = hitTestRegistry,
                selectedId = selectedId,
                hoveredId = hoveredId,
                dropTargetId = dropTargetId,
                modifier = if (isScaffoldContent) Modifier.fillMaxSize() else Modifier,
            )
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                hitTestRegistry.record(node.id, coordinates.boundsInRoot())
            }
            .drawOutline(
                selected = node.id == selectedId,
                hovered = node.id == hoveredId,
                dropTarget = node.id == dropTargetId,
            )
    ) {
        renderer(node, slotRenderer)
    }
}

private fun Modifier.drawOutline(
    selected: Boolean,
    hovered: Boolean,
    dropTarget: Boolean,
): Modifier = drawBehind {
    when {
        dropTarget -> drawRect(
            color = Color(0xFF4CAF50),
            style = Stroke(width = 4f),
        )
        selected -> drawRect(
            color = Color(0xFF6750A4),
            style = Stroke(width = 3f),
        )
        hovered -> drawRect(
            color = Color(0x886750A4),
            style = Stroke(width = 2f),
        )
    }
}

@Composable
private fun MissingRendererPlaceholder(node: AtomicNode) {
    Box(Modifier) {
        Text(
            text = "No renderer for ${node.type.value}",
            color = MaterialTheme.colorScheme.error,
        )
    }
}