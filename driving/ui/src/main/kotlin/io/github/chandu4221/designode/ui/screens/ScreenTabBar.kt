package io.github.chandu4221.designode.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
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
import io.github.chandu4221.designode.ui.ScreenTab

@Composable
fun ScreenTabBar(
    screens: List<ScreenTab>,
    activeScreenId: ScreenId?,
    onSwitchScreen: (ScreenId) -> Unit,
    onAddScreen: () -> Unit,
    onRenameScreen: (ScreenId, String) -> Unit,
    onRemoveScreen: (ScreenId) -> Unit,
    onSetStartScreen: (ScreenId) -> Unit,
    modifier: Modifier = Modifier,
) {
    var screenToRename by remember { mutableStateOf<ScreenTab?>(null) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth().height(42.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                screens.forEach { screen ->
                    val isActive = screen.id == activeScreenId
                    ScreenTabItem(
                        screen = screen,
                        isActive = isActive,
                        canDelete = screens.size > 1,
                        onSelect = { onSwitchScreen(screen.id) },
                        onRename = { screenToRename = screen },
                        onDelete = { onRemoveScreen(screen.id) },
                        onSetStart = { onSetStartScreen(screen.id) },
                    )
                }
            }

            VerticalDivider(
                modifier = Modifier
                    .height(20.dp)
                    .padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            // Add Screen Button
            IconButton(
                onClick = onAddScreen,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add screen",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
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
private fun ScreenTabItem(
    screen: ScreenTab,
    isActive: Boolean,
    canDelete: Boolean,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onSetStart: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val tabColor = if (isActive) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.Transparent
    }
    val contentColor = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(tabColor)
            .border(
                width = if (isActive) 1.dp else 0.dp,
                color = if (isActive) MaterialTheme.colorScheme.outlineVariant else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onSelect)
            .padding(start = 10.dp, end = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (screen.isStart) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Start Screen",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = screen.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            // Screen Options Menu Button
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Screen options",
                        tint = contentColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp),
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename", style = MaterialTheme.typography.bodySmall) },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, Modifier.size(16.dp)) },
                        onClick = {
                            menuExpanded = false
                            onRename()
                        },
                    )
                    if (!screen.isStart) {
                        DropdownMenuItem(
                            text = { Text("Set as Start Screen", style = MaterialTheme.typography.bodySmall) },
                            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, Modifier.size(16.dp)) },
                            onClick = {
                                menuExpanded = false
                                onSetStart()
                            },
                        )
                    }
                    if (canDelete) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) },
                            leadingIcon = { Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            },
                        )
                    }
                }
            }

            // Quick Delete 'x' if multiple screens and active
            if (canDelete && isActive) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(20.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close screen",
                        tint = contentColor.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }
    }
}
