package io.github.chandu4221.designode.ui.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.MaterialColorRoles
import io.github.chandu4221.designode.domain.model.PropertyKind
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.spec.PropertySpec
import io.github.chandu4221.designode.ui.theme.resolveColorRole

/**
 * Renders an editor for one property. The editor widget is chosen by
 * [PropertySpec.kind]. Every change produces a [Value] that the caller
 * dispatches to the ViewModel.
 */
@Composable
fun PropertyEditor(
    spec: PropertySpec,
    currentValue: Value?,
    onValueChange: (Value?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = spec.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        when (spec.kind) {
            PropertyKind.TEXT -> TextPropertyEditor(currentValue, onValueChange)
            PropertyKind.BOOL -> BoolPropertyEditor(currentValue, onValueChange)
            PropertyKind.INT -> IntPropertyEditor(currentValue, onValueChange)
            PropertyKind.DP -> DpPropertyEditor(currentValue, onValueChange)
            PropertyKind.COLOR -> ColorPropertyEditor(currentValue, onValueChange)
            PropertyKind.ENUM -> EnumPropertyEditor(spec, currentValue, onValueChange)
        }
    }
}

@Composable
private fun TextPropertyEditor(current: Value?, onChange: (Value) -> Unit) {
    val initial = (current as? Value.Text)?.value.orEmpty()
    var text by remember(initial) { mutableStateOf(initial) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onChange(Value.Text(it))
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun BoolPropertyEditor(current: Value?, onChange: (Value) -> Unit) {
    val value = (current as? Value.Bool)?.value ?: false
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Switch(
            checked = value,
            onCheckedChange = { onChange(Value.Bool(it)) },
        )
    }
}

@Composable
private fun IntPropertyEditor(current: Value?, onChange: (Value) -> Unit) {
    val initial = (current as? Value.IntValue)?.value?.toString().orEmpty()
    var text by remember(initial) { mutableStateOf(initial) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            it.toIntOrNull()?.let { parsed -> onChange(Value.IntValue(parsed)) }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DpPropertyEditor(current: Value?, onChange: (Value) -> Unit) {
    val initial = (current as? Value.Dp)?.value?.toString().orEmpty()
    var text by remember(initial) { mutableStateOf(initial) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            it.toFloatOrNull()?.let { parsed -> onChange(Value.Dp(parsed)) }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall,
        suffix = { Text("dp", style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ColorPropertyEditor(current: Value?, onChange: (Value) -> Unit) {
    var mode by remember(current) { mutableStateOf(if (current is Value.Color) "custom" else "role") }

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FilterChip(
            selected = mode == "role",
            onClick = {
                mode = "role"
                onChange(Value.ColorRole(MaterialColorRoles.PRIMARY))
            },
            label = { Text("Theme Role", style = MaterialTheme.typography.labelSmall) },
        )
        FilterChip(
            selected = mode == "custom",
            onClick = {
                mode = "custom"
                val defaultColor = (current as? Value.Color)?.value ?: 0xFF000000L
                onChange(Value.Color(defaultColor))
            },
            label = { Text("Custom Hex", style = MaterialTheme.typography.labelSmall) },
        )
    }

    if (mode == "role") {
        val selectedRole = (current as? Value.ColorRole)?.role ?: MaterialColorRoles.PRIMARY
        var expanded by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                onClick = { expanded = true },
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(resolveColorRole(selectedRole))
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                    Text(
                        text = selectedRole,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                MaterialColorRoles.ALL.forEach { role ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(resolveColorRole(role))
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                )
                                Text(role, style = MaterialTheme.typography.bodySmall)
                            }
                        },
                        onClick = {
                            onChange(Value.ColorRole(role))
                            expanded = false
                        },
                    )
                }
            }
        }
    } else {
        val initial = (current as? Value.Color)?.value?.let { hex(it) }.orEmpty()
        var text by remember(initial) { mutableStateOf(initial) }
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                parseHex(it)?.let { parsed -> onChange(Value.Color(parsed)) }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall,
            placeholder = { Text("#AARRGGBB", style = MaterialTheme.typography.bodySmall) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EnumPropertyEditor(
    spec: PropertySpec,
    current: Value?,
    onChange: (Value) -> Unit,
) {
    val selected = (current as? Value.EnumValue)?.name
        ?: (spec.default as? Value.EnumValue)?.name
        ?: spec.enumOptions.firstOrNull()
        ?: ""
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = selected,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            spec.enumOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    onClick = {
                        onChange(Value.EnumValue(option))
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun hex(value: Long): String =
    "#" + value.toULong().toString(16).uppercase().padStart(8, '0')

private fun parseHex(input: String): Long? {
    val cleaned = input.removePrefix("#").trim()
    if (cleaned.isEmpty()) return null
    return cleaned.toULongOrNull(16)?.toLong()
}