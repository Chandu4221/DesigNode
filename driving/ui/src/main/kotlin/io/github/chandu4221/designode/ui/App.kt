package io.github.chandu4221.designode.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
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
    val focusRequester = remember { FocusRequester() }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                    val isCtrlOrMeta = keyEvent.isCtrlPressed || keyEvent.isMetaPressed
                    when {
                        isCtrlOrMeta && keyEvent.isShiftPressed && keyEvent.key == Key.Z -> {
                            viewModel.redo()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.Y -> {
                            viewModel.redo()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.Z -> {
                            viewModel.undo()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.C -> {
                            viewModel.copySelected()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.V -> {
                            viewModel.paste()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.D -> {
                            viewModel.duplicateSelected()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.S -> {
                            viewModel.save()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.N -> {
                            viewModel.newProject()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.O -> {
                            viewModel.openProjectPicker()
                            true
                        }
                        isCtrlOrMeta && keyEvent.key == Key.E -> {
                            viewModel.openExportDialog()
                            true
                        }
                        keyEvent.key == Key.Escape -> {
                            when {
                                state.exportDialogOpen -> viewModel.closeExportDialog()
                                state.projectPickerOpen -> viewModel.closeProjectPicker()
                                state.themeDialogOpen -> viewModel.closeThemeDialog()
                                else -> viewModel.deselect()
                            }
                            true
                        }
                        keyEvent.key == Key.Delete || keyEvent.key == Key.Backspace -> {
                            val sel = state.selectedId
                            if (sel != null && sel != state.root.id) {
                                viewModel.removeNode(sel)
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                },
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                UnifiedTopBar(
                    projectName = state.projectName,
                    screens = state.screens,
                    activeScreenId = state.activeScreenId,
                    canUndo = state.canUndo,
                    canRedo = state.canRedo,
                    isDark = state.theme.isDark,
                    onUndo = viewModel::undo,
                    onRedo = viewModel::redo,
                    onToggleDarkMode = viewModel::toggleThemeDarkMode,
                    onOpenTheme = viewModel::openThemeDialog,
                    onSwitchScreen = viewModel::switchScreen,
                    onAddScreen = { viewModel.addScreen() },
                    onRenameScreen = viewModel::renameScreen,
                    onRemoveScreen = viewModel::removeScreen,
                    onSetStartScreen = viewModel::setStartScreen,
                    onOpen = viewModel::openProjectPicker,
                    onSave = viewModel::save,
                    onExport = viewModel::openExportDialog,
                )
                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    ActivityBar(
                        activeItem = ActivityBarItem.EXPLORER,
                        onOpenTheme = viewModel::openThemeDialog,
                    )
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
                        onNodeSelected = { id ->
                            viewModel.selectNode(id)
                            focusRequester.requestFocus()
                        },
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
                        onNodeSelected = { id ->
                            viewModel.selectNode(id)
                            focusRequester.requestFocus()
                        },
                        onCopy = viewModel::copySelected,
                        onDuplicate = viewModel::duplicateSelected,
                        rootNode = state.root,
                        onModifiersChanged = viewModel::updateModifiers,
                        modifier = Modifier.width(280.dp),
                    )
                }
                StatusBar(
                    saveStatus = state.saveStatus,
                )
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

        // Drag Ghost under cursor
        val dragType = state.dragType
        val dragPosition = state.dragPosition
        if (dragType != null && dragPosition != null) {
            val spec = viewModel.spec(dragType)
            val label = spec?.label ?: dragType.value
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    shape = RoundedCornerShape(17.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = dragPosition.x - 20f
                            translationY = dragPosition.y - 20f
                            alpha = 0.92f
                        }
                        .height(34.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = io.github.chandu4221.designode.ui.palette.iconFor(dragType.value),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        }
    }
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