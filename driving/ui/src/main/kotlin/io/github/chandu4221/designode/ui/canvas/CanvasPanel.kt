package io.github.chandu4221.designode.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.NodeId

@Composable
fun CanvasPanel(
    root: AtomicNode,
    renderers: PreviewRenderers,
    selectedId: NodeId?,
    hoveredId: NodeId?,
    onNodeSelected: (NodeId?) -> Unit,
    onNodeHovered: (NodeId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hitTestRegistry = remember { HitTestRegistry() }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(360.dp)
                .height(720.dp),
        ) {
            DeviceFrame {
                NodePreview(
                    node = root,
                    renderers = renderers,
                    hitTestRegistry = hitTestRegistry,
                    selectedId = selectedId,
                    hoveredId = hoveredId,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            // Overlay on top — intercepts all input for the frame area.
            SelectionOverlay(
                hitTestRegistry = hitTestRegistry,
                onNodeSelected = onNodeSelected,
                onNodeHovered = onNodeHovered,
            )
        }
    }
}

@Composable
private fun DeviceFrame(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface),
        ) {
            content()
        }
    }
}