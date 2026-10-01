package io.github.chandu4221.designode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.ScreenId

@Composable
fun UnifiedTopBar(
    projectName: String,
    screens: List<ScreenTab>,
    activeScreenId: ScreenId?,
    canUndo: Boolean,
    canRedo: Boolean,
    isDark: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onOpenTheme: () -> Unit,
    onSwitchScreen: (ScreenId) -> Unit,
    onAddScreen: () -> Unit,
    onRenameScreen: (ScreenId, String) -> Unit,
    onRemoveScreen: (ScreenId) -> Unit,
    onSetStartScreen: (ScreenId) -> Unit,
    onOpen: () -> Unit,
    onSave: () -> Unit,
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeScreen = screens.firstOrNull { it.id == activeScreenId } ?: screens.firstOrNull()
    var screenToRename by remember { mutableStateOf<ScreenTab?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 1. macOS Window Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(11.dp).clip(CircleShape).background(Color(0xFFED6A5E)))
                Box(Modifier.size(11.dp).clip(CircleShape).background(Color(0xFFF5BF4F)))
                Box(Modifier.size(11.dp).clip(CircleShape).background(Color(0xFF61C554)))
            }

            Spacer(Modifier.width(16.dp))

            // 2. Hamburger Menu Button
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Open Project") },
                        leadingIcon = { Icon(Icons.Default.FolderOpen, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onOpen()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Save Project") },
                        leadingIcon = { Icon(Icons.Default.Save, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onSave()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Theme Settings") },
                        leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null) },
                        onClick = {
                            menuExpanded = false
                            onOpenTheme()
                        },
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // 3. Project & File Breadcrumb
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Project: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
                Text(
                    text = projectName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = " / ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
                Text(
                    text = "${activeScreen?.name?.lowercase()?.replace(" ", "_") ?: "screen"}.kt",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.width(8.dp))

            // Undo / Redo
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo (Ctrl+Z)",
                    modifier = Modifier.size(16.dp),
                    tint = if (canUndo) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                )
            }
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo (Ctrl+Shift+Z)",
                    modifier = Modifier.size(16.dp),
                    tint = if (canRedo) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                )
            }

            Spacer(Modifier.weight(1f))

            // 4. Center Screen Pills
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                screens.forEach { screen ->
                    val isActive = screen.id == activeScreenId
                    ScreenPillTab(
                        screen = screen,
                        isActive = isActive,
                        canDelete = screens.size > 1,
                        onSelect = { onSwitchScreen(screen.id) },
                        onRename = { screenToRename = screen },
                        onDelete = { onRemoveScreen(screen.id) },
                        onSetStart = { onSetStartScreen(screen.id) },
                    )
                }

                // Plus button
                IconButton(
                    onClick = onAddScreen,
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add screen",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // 5. Right Action Controls
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDark) "Switch to light mode" else "Switch to dark mode",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(
                onClick = onSave,
                modifier = Modifier.size(32.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            // Export Code Terracotta Pill Button
            Surface(
                onClick = onExport,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(32.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = "Export Code",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }

    if (screenToRename != null) {
        val target = screenToRename!!
        var nameInput by remember { mutableStateOf(target.name) }
        AlertDialog(
            onDismissRequest = { screenToRename = null },
            title = { Text("Rename Screen", style = MaterialTheme.typography.titleMedium) },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    singleLine = true,
                    label = { Text("Screen Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRenameScreen(target.id, nameInput)
                        screenToRename = null
                    }
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { screenToRename = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun ScreenPillTab(
    screen: ScreenTab,
    isActive: Boolean,
    canDelete: Boolean,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onSetStart: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val pillBackground = if (isActive) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    val pillBorder = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(pillBackground)
            .border(
                width = if (isActive) 1.5.dp else 0.dp,
                color = pillBorder,
                shape = RoundedCornerShape(15.dp),
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Smartphone,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
            Text(
                text = screen.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(18.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename", style = MaterialTheme.typography.bodySmall) },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, Modifier.size(14.dp)) },
                        onClick = {
                            menuExpanded = false
                            onRename()
                        },
                    )
                    if (!screen.isStart) {
                        DropdownMenuItem(
                            text = { Text("Set as Start Screen", style = MaterialTheme.typography.bodySmall) },
                            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, Modifier.size(14.dp)) },
                            onClick = {
                                menuExpanded = false
                                onSetStart()
                            },
                        )
                    }
                    if (canDelete) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) },
                            leadingIcon = { Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp)) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            },
                        )
                    }
                }
            }
        }
    }
}
