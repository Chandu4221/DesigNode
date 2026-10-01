package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.ui.canvas.PreviewRenderer

private fun horizontalArrangement(name: String): Arrangement.Horizontal = when (name) {
    "end" -> Arrangement.End
    "center" -> Arrangement.Center
    "spaceBetween" -> Arrangement.SpaceBetween
    "spaceAround" -> Arrangement.SpaceAround
    "spaceEvenly" -> Arrangement.SpaceEvenly
    else -> Arrangement.Start
}

private fun verticalArrangement(name: String): Arrangement.Vertical = when (name) {
    "bottom" -> Arrangement.Bottom
    "center" -> Arrangement.Center
    "spaceBetween" -> Arrangement.SpaceBetween
    "spaceAround" -> Arrangement.SpaceAround
    "spaceEvenly" -> Arrangement.SpaceEvenly
    else -> Arrangement.Top
}

private fun horizontalAlignment(name: String): Alignment.Horizontal = when (name) {
    "centerHorizontally" -> Alignment.CenterHorizontally
    "end" -> Alignment.End
    else -> Alignment.Start
}

private fun verticalAlignment(name: String): Alignment.Vertical = when (name) {
    "centerVertically" -> Alignment.CenterVertically
    "bottom" -> Alignment.Bottom
    else -> Alignment.Top
}

private fun boxAlignment(name: String): Alignment = when (name) {
    "center" -> Alignment.Center
    "topCenter" -> Alignment.TopCenter
    "topEnd" -> Alignment.TopEnd
    "centerStart" -> Alignment.CenterStart
    "centerEnd" -> Alignment.CenterEnd
    "bottomStart" -> Alignment.BottomStart
    "bottomCenter" -> Alignment.BottomCenter
    "bottomEnd" -> Alignment.BottomEnd
    else -> Alignment.TopStart
}

internal val ColumnPreview: PreviewRenderer = { node, slots ->
    Column(
        verticalArrangement = verticalArrangement(
            node.properties.enumValue("verticalArrangement", "top")
        ),
        horizontalAlignment = horizontalAlignment(
            node.properties.enumValue("horizontalAlignment", "start")
        ),
    ) { slots(SlotId("content")) }
}

internal val RowPreview: PreviewRenderer = { node, slots ->
    Row(
        horizontalArrangement = horizontalArrangement(
            node.properties.enumValue("horizontalArrangement", "start")
        ),
        verticalAlignment = verticalAlignment(
            node.properties.enumValue("verticalAlignment", "top")
        ),
    ) { slots(SlotId("content")) }
}

internal val BoxPreview: PreviewRenderer = { node, slots ->
    Box(
        contentAlignment = boxAlignment(
            node.properties.enumValue("contentAlignment", "topStart")
        ),
        modifier = Modifier.fillMaxSize(),
    ) { slots(SlotId("content")) }
}

fun layoutRenderers(): Map<ComponentTypeId, PreviewRenderer> = mapOf(
    ComponentTypeId("Column") to ColumnPreview,
    ComponentTypeId("Row") to RowPreview,
    ComponentTypeId("Box") to BoxPreview,
)