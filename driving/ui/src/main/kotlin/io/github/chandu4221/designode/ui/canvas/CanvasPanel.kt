package io.github.chandu4221.designode.ui.canvas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.luminance
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
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val canvasBg = if (isDark) Color(0xFF181210) else Color(0xFFEDE4DD)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clipToBounds()
            .background(canvasBg)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun DeviceFrame(content: @Composable () -> Unit) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val bezelBg = if (isDark) Color(0xFF1E1613) else Color(0xFF28201C)
    val bezelBorder = if (isDark) Color(0xFF382922) else Color(0xFF453630)

    // Outer phone bezel
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(36.dp))
            .background(bezelBg)
            .border(
                width = 3.dp,
                color = bezelBorder,
                shape = RoundedCornerShape(36.dp),
            )
            .padding(5.dp),
    ) {
        // Inner screen surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(31.dp))
                .background(MaterialTheme.colorScheme.background),
        ) {
            content()

            // Realistic Device Top Status Bar
            PhoneStatusBar(modifier = Modifier.align(Alignment.TopCenter))

            // Realistic Bottom Home Indicator Line
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .width(76.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
private fun PhoneStatusBar(modifier: Modifier = Modifier) {
    val iconColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(26.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "12:30",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = iconColor,
        )
        // Center Camera Punch-hole
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(0xFF0C0908))
                .border(1.dp, Color(0xFF281E19), CircleShape)
        )
        // Right System Indicators: Signal & Battery
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Signal Bars
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            ) {
                Box(Modifier.width(2.5.dp).height(4.dp).background(iconColor))
                Box(Modifier.width(2.5.dp).height(7.dp).background(iconColor))
                Box(Modifier.width(2.5.dp).height(10.dp).background(iconColor))
            }
            // Battery icon (pill + cap)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(9.dp)
                        .border(1.dp, iconColor, RoundedCornerShape(2.dp))
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.85f)
                            .background(iconColor, RoundedCornerShape(1.dp))
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(4.dp)
                        .background(iconColor, RoundedCornerShape(topEnd = 1.dp, bottomEnd = 1.dp))
                )
            }
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