package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.ui.canvas.PreviewRenderer

private fun textOf(properties: Map<PropertyKey, Value>, key: String, default: String = ""): String =
    (properties[PropertyKey(key)] as? Value.Text)?.value ?: default

private fun enumOf(properties: Map<PropertyKey, Value>, key: String, default: String = ""): String =
    (properties[PropertyKey(key)] as? Value.EnumValue)?.name ?: default

private fun colorOf(properties: Map<PropertyKey, Value>, key: String, default: Long = 0xFF000000L): Long =
    (properties[PropertyKey(key)] as? Value.Color)?.value ?: default

private fun dpOf(properties: Map<PropertyKey, Value>, key: String, default: Float = 0f): Float =
    (properties[PropertyKey(key)] as? Value.Dp)?.value ?: default

private fun textAlignOf(align: String): TextAlign = when (align) {
    "center" -> TextAlign.Center
    "end" -> TextAlign.End
    "justify" -> TextAlign.Justify
    else -> TextAlign.Start
}

internal val TextPreview: PreviewRenderer = { node, _ ->
    Text(
        text = textOf(node.properties, "text", ""),
        style = MaterialTheme.typography.bodyMedium,
        color = Color(colorOf(node.properties, "color")),
        textAlign = textAlignOf(enumOf(node.properties, "textAlign", "start")),
    )
}

internal val IconPreview: PreviewRenderer = { node, _ ->
    Icon(
        imageVector = androidx.compose.material.icons.Icons.Default.Star,
        contentDescription = textOf(node.properties, "contentDescription", ""),
        modifier = Modifier.size(dpOf(node.properties, "size", 24f).dp),
        tint = Color(colorOf(node.properties, "tint")),
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