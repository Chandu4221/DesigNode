package io.github.chandu4221.designode.ui.inspector

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.PropertyKind
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.spec.PropertySpec

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