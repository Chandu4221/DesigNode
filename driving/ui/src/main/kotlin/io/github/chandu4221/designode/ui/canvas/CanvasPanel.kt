package io.github.chandu4221.designode.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.nodes
import io.github.chandu4221.designode.ui.DropTarget

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CanvasPanel(
    root: AtomicNode,
    renderers: PreviewRenderers,
    hitTestRegistry: HitTestRegistry,
    selectedId: NodeId?,
    hoveredId: NodeId?,
    dropTarget: DropTarget? = null,
    dropTargetId: NodeId? = dropTarget?.nodeId,
    dragType: ComponentTypeId? = null,
    onNodeSelected: (NodeId?) -> Unit,
    onNodeHovered: (NodeId?) -> Unit,
    modifier: Modifier = Modifier,
    transformState: CanvasTransformState = rememberCanvasTransformState(),
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clipToBounds()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .onGloballyPositioned { coordinates ->
                transformState.containerSize = coordinates.size
            }
            .onPointerEvent(PointerEventType.Scroll) { event ->
                val change = event.changes.firstOrNull() ?: return@onPointerEvent
                val deltaY = change.scrollDelta.y
                if (deltaY < 0f) {
                    transformState.zoomByFactor(1.1f)
                } else if (deltaY > 0f) {
                    transformState.zoomByFactor(0.9f)
                }
            }
            .onPointerEvent(PointerEventType.Move) { event ->
                val isMiddleDrag = event.buttons.isTertiaryPressed
                val isModifierDrag = (event.keyboardModifiers.isCtrlPressed || event.keyboardModifiers.isMetaPressed) && event.buttons.isPrimaryPressed
                if (isMiddleDrag || isModifierDrag) {
                    val change = event.changes.firstOrNull() ?: return@onPointerEvent
                    val delta = change.position - change.previousPosition
                    if (delta != Offset.Zero) {
                        transformState.panBy(delta)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    transformState.panBy(dragAmount)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = transformState.zoom
                    scaleY = transformState.zoom
                    translationX = transformState.pan.x
                    translationY = transformState.pan.y
                }
                .width(360.dp)
                .height(720.dp),
        ) {
            CompositionLocalProvider(
                LocalHitTestRegistry provides hitTestRegistry,
                LocalDropTarget provides dropTarget,
                LocalDragType provides dragType,
            ) {
                DeviceFrame {
                    if (root.isPristineScaffold()) {
                        EmptyCanvasHint()
                    }
                    NodePreview(
                        node = root,
                        renderers = renderers,
                        hitTestRegistry = hitTestRegistry,
                        selectedId = selectedId,
                        hoveredId = hoveredId,
                        dropTargetId = dropTarget?.nodeId ?: dropTargetId,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            SelectionOverlay(
                hitTestRegistry = hitTestRegistry,
                onNodeSelected = onNodeSelected,
                onNodeHovered = onNodeHovered,
            )
        }

        ZoomControls(
            zoom = transformState.zoom,
            onZoomIn = { transformState.zoomIn() },
            onZoomOut = { transformState.zoomOut() },
            onResetZoom = { transformState.resetZoom() },
            onFitToScreen = { transformState.fitToScreen() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        )
    }
}

@Composable
private fun EmptyCanvasHint() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Drag a component\nfrom the palette",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
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
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.background),
        ) {
            content()
        }
    }
}

/**
 * True when the tree is exactly the default: Scaffold with a single,
 * childless Column in its content slot. Any insertion flips this to false.
 */
private fun AtomicNode.isPristineScaffold(): Boolean {
    if (type.value != "Scaffold") return false
    val content = slots[SlotId("content")]?.nodes().orEmpty()
    if (content.size != 1) return false
    val column = content.first()
    if (column.type.value != "Column") return false
    return column.slots.values.all { it == SlotContent.Empty }
}