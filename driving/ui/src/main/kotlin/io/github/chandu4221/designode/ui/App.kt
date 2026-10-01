package io.github.chandu4221.designode.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.nodes
import io.github.chandu4221.designode.ui.canvas.CanvasPanel
import io.github.chandu4221.designode.ui.canvas.PreviewRenderersRegistry
import io.github.chandu4221.designode.ui.inspector.InspectorPanel
import io.github.chandu4221.designode.ui.palette.PalettePanel

@Composable
fun App(viewModel: EditorViewModel) {
    val state by viewModel.state.collectAsState()
    val renderers = remember { PreviewRenderersRegistry.build() }

    val selectedNode = state.selectedId?.let { state.root.findNode(it) }
    val selectedSpec = selectedNode?.let { viewModel.spec(it.type) }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar()
                Row(modifier = Modifier.fillMaxSize()) {
                    PalettePanel(
                        specs = viewModel.specs,
                        modifier = Modifier.width(240.dp),
                    )
                    CanvasPanel(
                        root = state.root,
                        renderers = renderers,
                        selectedId = state.selectedId,
                        hoveredId = state.hoveredId,
                        onNodeSelected = viewModel::selectNode,
                        onNodeHovered = viewModel::hoverNode,
                        modifier = Modifier.weight(1f),
                    )
                    InspectorPanel(
                        selected = selectedNode,
                        spec = selectedSpec,
                        rootId = state.root.id,
                        onVariantChanged = viewModel::updateVariant,
                        onPropertyChanged = viewModel::updateProperty,
                        onRemove = viewModel::removeNode,
                        modifier = Modifier.width(280.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBar() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "DesigNode",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Walks the tree to find the node with the given ID. Local helper — the
 * full-tree search lives in NodeTree but the UI only holds a snapshot.
 */
private fun io.github.chandu4221.designode.domain.model.AtomicNode.findNode(
    id: io.github.chandu4221.designode.domain.model.NodeId,
): io.github.chandu4221.designode.domain.model.AtomicNode? {
    if (this.id == id) return this
    slots.values.forEach { content ->
        content.nodes().forEach { child ->
            child.findNode(id)?.let { return it }
        }
    }
    return null
}