package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.ui.canvas.PreviewRenderer

private fun textAlignOf(name: String): TextAlign = when (name) {
    "center" -> TextAlign.Center
    "end" -> TextAlign.End
    "justify" -> TextAlign.Justify
    else -> TextAlign.Start
}

internal val TextPreview: PreviewRenderer = { node, _ ->
    Text(
        text = node.properties.text("text"),
        style = MaterialTheme.typography.bodyMedium,
        color = node.properties.composeColor("color"),
        textAlign = textAlignOf(node.properties.enumValue("textAlign", "start")),
    )
}

internal val IconPreview: PreviewRenderer = { node, _ ->
    val iconName = node.properties.text("name", "Favorite")
    Icon(
        imageVector = io.github.chandu4221.designode.ui.icon.MaterialIconRegistry.findOrDefault(iconName),
        contentDescription = node.properties.text("contentDescription"),
        modifier = Modifier.size(node.properties.dp("size", 24f).dp),
        tint = node.properties.composeColor("tint"),
    )
}

internal val SpacerPreview: PreviewRenderer = { _, _ ->
    Spacer(Modifier.size(16.dp))
}

fun atomRenderers(): Map<ComponentTypeId, PreviewRenderer> = mapOf(
    ComponentTypeId("Text") to TextPreview,
    ComponentTypeId("Icon") to IconPreview,
    ComponentTypeId("Spacer") to SpacerPreview,
)