package io.github.chandu4221.designode.ui.canvas.renderers

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.ui.canvas.PreviewRenderer

// ─────────────────────────────────────────────────────
// TopAppBar — 4 variants
// ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
private val TopAppBarPreview: PreviewRenderer = { node, slots ->
    val title: @Composable () -> Unit = {
        slots(SlotId("title"))
    }
    val navigationIcon: @Composable () -> Unit = {
        slots(SlotId("navigationIcon"))
    }
    val actions: @Composable RowScope.() -> Unit = {
        slots(SlotId("actions"))
    }

    when (node.variant?.value) {
        "centerAligned" -> CenterAlignedTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
        )

        "medium" -> MediumTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
        )

        "large" -> LargeTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
        )

        else -> TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
        )
    }
}

// ─────────────────────────────────────────────────────
// NavigationBar
// ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
private val NavigationBarPreview: PreviewRenderer = { _, slots ->
    NavigationBar {
        slots(SlotId("items"))
    }
}

// ─────────────────────────────────────────────────────
// NavigationBarItem
// ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
private val NavigationBarItemPreview: PreviewRenderer = { _, slots ->
    // NavigationBarItem is a RowScope extension. Our SlotRenderer doesn't
    // propagate the parent's RowScope, so we wrap in a Row to acquire one.
    // Approximate visual — the real fix threads scope through SlotRenderer
    // once the overlay work lands.
    androidx.compose.foundation.layout.Row {
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { slots(SlotId("icon")) },
            label = { slots(SlotId("label")) },
        )
    }
}

fun barRenderers(): Map<ComponentTypeId, PreviewRenderer> = mapOf(
    ComponentTypeId("TopAppBar") to TopAppBarPreview,
    ComponentTypeId("NavigationBar") to NavigationBarPreview,
    ComponentTypeId("NavigationBarItem") to NavigationBarItemPreview,
)