package io.github.chandu4221.designode.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import io.github.chandu4221.designode.ui.canvas.CanvasPanel
import io.github.chandu4221.designode.ui.canvas.PreviewRenderersRegistry
import io.github.chandu4221.designode.ui.palette.PalettePanel

@Composable
fun App(viewModel: EditorViewModel) {
    val state by viewModel.state.collectAsState()
    val renderers = remember { PreviewRenderersRegistry.build() }

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
                        modifier = Modifier.weight(1f),
                    )
                    InspectorPanel(modifier = Modifier.width(280.dp))
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

@Composable
private fun InspectorPanel(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxHeight(),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Inspector",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}