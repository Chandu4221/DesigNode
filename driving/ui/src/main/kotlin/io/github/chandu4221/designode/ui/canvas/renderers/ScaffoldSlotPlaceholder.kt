package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.ui.canvas.LocalDragType
import io.github.chandu4221.designode.ui.canvas.LocalDropTarget
import io.github.chandu4221.designode.ui.canvas.LocalHitTestRegistry

/**
 * A dashed placeholder drop zone rendered inside empty Scaffold slots.
 * Automatically measures bounds, registers with [LocalHitTestRegistry], and
 * displays dynamic highlight states when targeted during a drag gesture.
 */
@Composable
fun ScaffoldSlotPlaceholder(
    nodeId: NodeId,
    slotId: SlotId,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = Icons.Default.Add,
    acceptedTypes: Set<ComponentTypeId> = emptySet(),
    cornerRadius: Dp = 8.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
) {
    val hitTestRegistry = LocalHitTestRegistry.current
    val dropTarget = LocalDropTarget.current
    val dragType = LocalDragType.current

    val isTarget = dropTarget != null && dropTarget.nodeId == nodeId && dropTarget.slotId == slotId
    val isCompatibleDrag = dragType != null && (acceptedTypes.isEmpty() || dragType in acceptedTypes)

    val strokeColor = when {
        isTarget -> Color(0xFF388E3C)
        isCompatibleDrag -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    val backgroundColor = when {
        isTarget -> Color(0x2E4CAF50)
        isCompatibleDrag -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    }

    val contentColor = when {
        isTarget -> Color(0xFF1B5E20)
        isCompatibleDrag -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderWidth = when {
        isTarget -> 2.5.dp
        isCompatibleDrag -> 2.dp
        else -> 1.5.dp
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                hitTestRegistry?.recordSlot(nodeId, slotId, coordinates.boundsInRoot())
            }
            .clip(shape)
            .background(backgroundColor)
            .drawDashedBorder(
                width = borderWidth,
                color = strokeColor,
                cornerRadius = cornerRadius,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor,
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isTarget || isCompatibleDrag) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor,
            )
        }
    }
}

private fun Modifier.drawDashedBorder(
    width: Dp,
    color: Color,
    cornerRadius: Dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 4.dp,
): Modifier = drawBehind {
    val strokeWidth = width.toPx()
    val halfStroke = strokeWidth / 2f
    val arcRadius = cornerRadius.toPx()
    val stroke = Stroke(
        width = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            0f,
        ),
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(halfStroke, halfStroke),
        size = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = CornerRadius(arcRadius, arcRadius),
        style = stroke,
    )
}
