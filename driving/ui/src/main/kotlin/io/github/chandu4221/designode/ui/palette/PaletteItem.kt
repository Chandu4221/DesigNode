package io.github.chandu4221.designode.ui.palette

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import kotlin.math.sqrt

private const val DRAG_THRESHOLD_PX = 6f

@Composable
fun PaletteItem(
    spec: ComponentSpec,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDragMove: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var coordinates: LayoutCoordinates? by remember { mutableStateOf(null) }

    Row(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(
                if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.surfaceContainerHigh
            )
            .border(
                width = 1.dp,
                color = if (isDragging) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(17.dp),
            )
            .alpha(if (isDragging) 0.6f else 1f)
            .onGloballyPositioned { coordinates = it }
            .pointerInput(spec.type) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var dragging = false

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break

                        if (!dragging) {
                            val distance = distanceBetween(down.position, change.position)
                            if (distance > DRAG_THRESHOLD_PX) {
                                dragging = true
                                onDragStart()
                            }
                        }

                        if (dragging) {
                            val rootPos = coordinates?.localToRoot(change.position)
                                ?: change.position
                            onDragMove(rootPos)
                            change.consume()
                        }

                        if (!change.pressed) {
                            if (dragging) onDragEnd() else onDragCancel()
                            break
                        }
                    }
                }
            }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = iconFor(spec.type.value),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = spec.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun distanceBetween(a: Offset, b: Offset): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    return sqrt(dx * dx + dy * dy)
}