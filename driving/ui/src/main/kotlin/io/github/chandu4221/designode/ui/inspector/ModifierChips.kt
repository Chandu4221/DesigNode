package io.github.chandu4221.designode.ui.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.chandu4221.designode.domain.model.ModifierToken

/**
 * Modifier section in the Inspector matching Gemini_Generated_Image_oo70jxoo70.png:
 * - Label "Modifier" with a dropdown selector ("-- ⌵")
 * - FlowRow of removable chips (e.g. "fillMaxSize ✕", "padding(16.dp) ✕")
 * - Clicking a chip opens a parameter edit dialog.
 */
@Composable
fun ModifierSection(
    modifiers: List<ModifierToken>,
    onModifiersChanged: (List<ModifierToken>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expandedDropdown by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        // Header row: "Modifier" label
        Text(
            text = "Modifier",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(6.dp))

        // Dropdown selector button: "-- ⌵"
        Box(Modifier.fillMaxWidth()) {
            Surface(
                onClick = { expandedDropdown = true },
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "+ Add modifier…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Add modifier",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            DropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false },
            ) {
                DropdownMenuItem(
                    text = { Text("fillMaxSize()", style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        val updated = modifiers + listOf(
                            ModifierToken.FillMaxWidth(1f),
                            ModifierToken.FillMaxHeight(1f),
                        )
                        onModifiersChanged(updated)
                        expandedDropdown = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("padding(16.dp)", style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onModifiersChanged(modifiers + ModifierToken.Padding(16f))
                        expandedDropdown = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("fillMaxWidth()", style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onModifiersChanged(modifiers + ModifierToken.FillMaxWidth(1f))
                        expandedDropdown = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("fillMaxHeight()", style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onModifiersChanged(modifiers + ModifierToken.FillMaxHeight(1f))
                        expandedDropdown = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("size(width, height)", style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onModifiersChanged(modifiers + ModifierToken.Size(48f, 48f))
                        expandedDropdown = false
                    },
                )
                DropdownMenuItem(
                    text = { Text("weight(1.0)", style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onModifiersChanged(modifiers + ModifierToken.Weight(1f))
                        expandedDropdown = false
                    },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Flow of active modifier chips
        if (modifiers.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                modifiers.forEachIndexed { index, token ->
                    ModifierChip(
                        token = token,
                        onClick = { editingIndex = index },
                        onRemove = {
                            val updated = modifiers.toMutableList()
                            updated.removeAt(index)
                            onModifiersChanged(updated)
                        },
                    )
                }
            }
        }
    }

    // Parameter Edit Dialog if a chip was clicked
    editingIndex?.let { index ->
        if (index in modifiers.indices) {
            val token = modifiers[index]
            ModifierEditDialog(
                token = token,
                onDismiss = { editingIndex = null },
                onSave = { updatedToken ->
                    val updated = modifiers.toMutableList()
                    updated[index] = updatedToken
                    onModifiersChanged(updated)
                    editingIndex = null
                },
            )
        } else {
            editingIndex = null
        }
    }
}

@Composable
private fun ModifierChip(
    token: ModifierToken,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.clip(RoundedCornerShape(14.dp)),
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = formatTokenLabel(token),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(end = 4.dp),
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove ${formatTokenLabel(token)}",
                    modifier = Modifier.size(13.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun formatTokenLabel(token: ModifierToken): String = when (token) {
    is ModifierToken.Padding -> "padding(${token.all.toInt()}.dp)"
    is ModifierToken.Size -> {
        val w = token.width?.let { "${it.toInt()}.dp" } ?: "wrap"
        val h = token.height?.let { "${it.toInt()}.dp" } ?: "wrap"
        "size($w, $h)"
    }
    is ModifierToken.FillMaxWidth -> if (token.fraction == 1f) "fillMaxWidth" else "fillMaxWidth(${token.fraction})"
    is ModifierToken.FillMaxHeight -> if (token.fraction == 1f) "fillMaxHeight" else "fillMaxHeight(${token.fraction})"
    is ModifierToken.Weight -> "weight(${token.value})"
}

@Composable
private fun ModifierEditDialog(
    token: ModifierToken,
    onDismiss: () -> Unit,
    onSave: (ModifierToken) -> Unit,
) {
    var floatValue1 by remember {
        mutableStateOf(
            when (token) {
                is ModifierToken.Padding -> token.all.toString()
                is ModifierToken.Size -> (token.width ?: 0f).toString()
                is ModifierToken.FillMaxWidth -> token.fraction.toString()
                is ModifierToken.FillMaxHeight -> token.fraction.toString()
                is ModifierToken.Weight -> token.value.toString()
            }
        )
    }
    var floatValue2 by remember {
        mutableStateOf(
            when (token) {
                is ModifierToken.Size -> (token.height ?: 0f).toString()
                else -> ""
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(16.dp).width(280.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Edit Modifier",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(12.dp))

                when (token) {
                    is ModifierToken.Padding -> {
                        OutlinedTextField(
                            value = floatValue1,
                            onValueChange = { floatValue1 = it },
                            label = { Text("Padding (dp)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    is ModifierToken.Size -> {
                        OutlinedTextField(
                            value = floatValue1,
                            onValueChange = { floatValue1 = it },
                            label = { Text("Width (dp)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = floatValue2,
                            onValueChange = { floatValue2 = it },
                            label = { Text("Height (dp)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    is ModifierToken.FillMaxWidth, is ModifierToken.FillMaxHeight -> {
                        OutlinedTextField(
                            value = floatValue1,
                            onValueChange = { floatValue1 = it },
                            label = { Text("Fraction (0.0 - 1.0)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    is ModifierToken.Weight -> {
                        OutlinedTextField(
                            value = floatValue1,
                            onValueChange = { floatValue1 = it },
                            label = { Text("Weight") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val v1 = floatValue1.toFloatOrNull() ?: 0f
                            val v2 = floatValue2.toFloatOrNull() ?: 0f
                            val updated = when (token) {
                                is ModifierToken.Padding -> ModifierToken.Padding(v1)
                                is ModifierToken.Size -> ModifierToken.Size(v1, v2)
                                is ModifierToken.FillMaxWidth -> ModifierToken.FillMaxWidth(v1.coerceIn(0f, 1f))
                                is ModifierToken.FillMaxHeight -> ModifierToken.FillMaxHeight(v1.coerceIn(0f, 1f))
                                is ModifierToken.Weight -> ModifierToken.Weight(v1.coerceAtLeast(0f))
                            }
                            onSave(updated)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
