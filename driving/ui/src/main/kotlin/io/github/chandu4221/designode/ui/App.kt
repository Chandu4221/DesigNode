package io.github.chandu4221.designode.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.nodes
import io.github.chandu4221.designode.ui.canvas.CanvasPanel
import io.github.chandu4221.designode.ui.canvas.PreviewRenderersRegistry
import io.github.chandu4221.designode.ui.export.ExportDialog
import io.github.chandu4221.designode.ui.inspector.InspectorPanel
import io.github.chandu4221.designode.ui.palette.PalettePanel
import io.github.chandu4221.designode.ui.project.ProjectPickerDialog

import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import io.github.chandu4221.designode.ui.theme.ThemeDialog
import io.github.chandu4221.designode.ui.theme.parsePaletteStyle

@Composable
fun App(viewModel: EditorViewModel) {
    val state by viewModel.state.collectAsState()
    val projectList by viewModel.projectList.collectAsState()
    val renderers = remember { PreviewRenderersRegistry.build() }

    val selectedNode = state.selectedId?.let { state.root.findNode(it) }
    val selectedSpec = selectedNode?.let { viewModel.spec(it.type) }

    DynamicMaterialTheme(
        seedColor = Color(state.theme.seedColor),
        useDarkTheme = state.theme.isDark,
        style = parsePaletteStyle(state.theme.style),
        contrastLevel = state.theme.contrastLevel,
        animate = true,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar(
                    projectName = state.projectName,
                    saveStatus = state.saveStatus,
                    isDark = state.theme.isDark,
                    onToggleDarkMode = viewModel::toggleThemeDarkMode,
                    onOpenTheme = viewModel::openThemeDialog,
                    onNew = viewModel::newProject,
                    onOpen = viewModel::openProjectPicker,
                    onSave = viewModel::save,
                    onExport = viewModel::openExportDialog,
                )
                Row(modifier = Modifier.fillMaxSize()) {
                    PalettePanel(
                        specs = viewModel.specs,
                        draggingType = state.dragType,
                        onDragStart = viewModel::beginDrag,
                        onDragMove = viewModel::updateDragPosition,
                        onDragEnd = viewModel::commitDrag,
                        onDragCancel = viewModel::cancelDrag,
                        modifier = Modifier.width(240.dp),
                    )
                    CanvasPanel(
                        root = state.root,
                        renderers = renderers,
                        hitTestRegistry = viewModel.hitTestRegistry,
                        selectedId = state.selectedId,
                        hoveredId = state.hoveredId,
                        dropTarget = state.dropTarget,
                        dragType = state.dragType,
                        onNodeSelected = viewModel::selectNode,
                        onNodeHovered = viewModel::hoverNode,
                        modifier = Modifier.weight(1f),
                    )
                    InspectorPanel(
                        selected = selectedNode,
                        spec = selectedSpec,
                        rootId = state.root.id,
                        specLookup = viewModel::spec,
                        onVariantChanged = viewModel::updateVariant,
                        onPropertyChanged = viewModel::updateProperty,
                        onRemove = viewModel::removeNode,
                        onNodeSelected = viewModel::selectNode,
                        modifier = Modifier.width(280.dp),
                    )
                }
            }
        }

        if (state.exportDialogOpen) {
            var cachedCode by remember { mutableStateOf("") }
            androidx.compose.runtime.LaunchedEffect(state.exportDialogOpen) {
                cachedCode = viewModel.generateCode()
            }
            ExportDialog(code = cachedCode, onDismiss = viewModel::closeExportDialog)
        }

        if (state.projectPickerOpen) {
            ProjectPickerDialog(
                projects = projectList,
                onOpen = viewModel::loadProject,
                onDismiss = viewModel::closeProjectPicker,
            )
        }

        if (state.themeDialogOpen) {
            ThemeDialog(
                theme = state.theme,
                onSeedColorChange = viewModel::updateThemeSeedColor,
                onDarkModeToggle = viewModel::toggleThemeDarkMode,
                onContrastChange = viewModel::updateThemeContrast,
                onStyleChange = viewModel::updateThemeStyle,
                onDismiss = viewModel::closeThemeDialog,
            )
        }
    }
}

@Composable
private fun TopBar(
    projectName: String,
    saveStatus: SaveStatus,
    isDark: Boolean,
    onToggleDarkMode: () -> Unit,
    onOpenTheme: () -> Unit,
    onNew: () -> Unit,
    onOpen: () -> Unit,
    onSave: () -> Unit,
    onExport: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth().height(56.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "DesigNode",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "·",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = projectName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.weight(1f))

            SaveStatusBadge(saveStatus)

            IconButton(onClick = onToggleDarkMode) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDark) "Switch to light mode" else "Switch to dark mode",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onOpenTheme) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Theme settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onNew) {
                Icon(
                    imageVector = Icons.Default.CreateNewFolder,
                    contentDescription = "New project",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onOpen) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = "Open project",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onSave) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onExport) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text("Export")
            }
        }
    }
}

@Composable
private fun SaveStatusBadge(status: SaveStatus) {
    val (text, color) = when (status) {
        SaveStatus.Idle -> return
        SaveStatus.Saving -> "Saving…" to MaterialTheme.colorScheme.onSurfaceVariant
        SaveStatus.Saved -> "Saved" to MaterialTheme.colorScheme.primary
        is SaveStatus.Failed -> "Error" to MaterialTheme.colorScheme.error
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier.padding(end = 8.dp),
    )
}

private fun AtomicNode.findNode(id: NodeId): AtomicNode? {
    if (this.id == id) return this
    slots.values.forEach { content ->
        content.nodes().forEach { child ->
            child.findNode(id)?.let { return it }
        }
    }
    return null
}