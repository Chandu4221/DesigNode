package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.TextButton
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.ui.canvas.PreviewRenderer

// ─────────────────────────────────────────────────────
// Button — 5 variants
// ─────────────────────────────────────────────────────

private val ButtonPreview: PreviewRenderer = { node, slots ->
    val onClick: () -> Unit = {}
    val enabled = node.properties.bool("enabled", true)
    val content: @androidx.compose.runtime.Composable RowScope.() -> Unit = {
        slots(SlotId("content"))
    }

    when (node.variant?.value) {
        "outlined" -> OutlinedButton(onClick = onClick, enabled = enabled, content = content)
        "text" -> TextButton(onClick = onClick, enabled = enabled, content = content)
        "elevated" -> ElevatedButton(onClick = onClick, enabled = enabled, content = content)
        "tonal" -> FilledTonalButton(onClick = onClick, enabled = enabled, content = content)
        else -> Button(onClick = onClick, enabled = enabled, content = content)
    }
}

// ─────────────────────────────────────────────────────
// Card — 3 variants
// ─────────────────────────────────────────────────────

private val CardPreview: PreviewRenderer = { node, slots ->
    val content: @androidx.compose.runtime.Composable () -> Unit = {
        slots(SlotId("content"))
    }
    when (node.variant?.value) {
        "elevated" -> ElevatedCard { content() }
        "outlined" -> OutlinedCard { content() }
        else -> Card { content() }
    }
}

// ─────────────────────────────────────────────────────
// FloatingActionButton — 3 variants
// ─────────────────────────────────────────────────────

private val FabPreview: PreviewRenderer = { node, slots ->
    val content: @androidx.compose.runtime.Composable () -> Unit = {
        slots(SlotId("content"))
    }
    val onClick: () -> Unit = {}
    when (node.variant?.value) {
        "small" -> SmallFloatingActionButton(onClick = onClick) { content() }
        "large" -> LargeFloatingActionButton(onClick = onClick) { content() }
        else -> FloatingActionButton(onClick = onClick) { content() }
    }
}

// ─────────────────────────────────────────────────────
// ExtendedFloatingActionButton
// ─────────────────────────────────────────────────────

private val ExtendedFabPreview: PreviewRenderer = { node, slots ->
    ExtendedFloatingActionButton(
        onClick = {},
        icon = { slots(SlotId("icon")) },
        text = { slots(SlotId("text")) },
        expanded = node.properties.bool("expanded", true),
    )
}

fun containerRenderers(): Map<ComponentTypeId, PreviewRenderer> = mapOf(
    ComponentTypeId("Button") to ButtonPreview,
    ComponentTypeId("Card") to CardPreview,
    ComponentTypeId("FloatingActionButton") to FabPreview,
    ComponentTypeId("ExtendedFloatingActionButton") to ExtendedFabPreview,
)