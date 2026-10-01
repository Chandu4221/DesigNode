package io.github.chandu4221.designode.ui.canvas

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import io.github.chandu4221.designode.domain.model.NodeId

/**
 * Sits on top of the canvas preview and intercepts all input.
 *
 * - Left-click → hit-test → select the deepest node containing the point.
 *   Clicking outside any node deselects.
 * - Mouse move → hover the deepest node containing the pointer.
 * - Mouse exit → clear hover.
 *
 * The preview underneath renders real Material 3 composables, but they
 * never receive input because this overlay consumes every pointer event
 * before it reaches them.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectionOverlay(
    hitTestRegistry: HitTestRegistry,
    onNodeSelected: (NodeId?) -> Unit,
    onNodeHovered: (NodeId?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var coordinates: LayoutCoordinates? by remember { mutableStateOf(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates = it }
            .onPointerEvent(PointerEventType.Move) { event ->
                val change = event.changes.firstOrNull() ?: return@onPointerEvent
                val rootPoint = coordinates?.localToRoot(change.position) ?: change.position
                onNodeHovered(hitTestRegistry.hitTest(rootPoint.x, rootPoint.y))
            }
            .onPointerEvent(PointerEventType.Exit) {
                onNodeHovered(null)
            }
            .pointerInput(Unit) {
                detectTapGestures { localOffset ->
                    val rootPoint = coordinates?.localToRoot(localOffset) ?: localOffset
                    onNodeSelected(hitTestRegistry.hitTest(rootPoint.x, rootPoint.y))
                }
            }
    )
}