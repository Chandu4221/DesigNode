package io.github.chandu4221.designode.ui.inspector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.VariantId
import io.github.chandu4221.designode.domain.spec.ComponentSpec

@Composable
fun InspectorPanel(
    selected: AtomicNode?,
    spec: ComponentSpec?,
    rootId: NodeId,
    onVariantChanged: (NodeId, VariantId?) -> Unit,
    onPropertyChanged: (NodeId, PropertyKey, Value?) -> Unit,
    onRemove: (NodeId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxHeight(),
    ) {
        when {
            selected == null || spec == null -> EmptyInspector()
            else -> InspectorContent(
                node = selected,
                spec = spec,
                canDelete = selected.id != rootId,
                onVariantChanged = onVariantChanged,
                onPropertyChanged = onPropertyChanged,
                onRemove = onRemove,
            )
        }
    }
}

@Composable
private fun EmptyInspector() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Select a component",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InspectorContent(
    node: AtomicNode,
    spec: ComponentSpec,
    canDelete: Boolean,
    onVariantChanged: (NodeId, VariantId?) -> Unit,
    onPropertyChanged: (NodeId, PropertyKey, Value?) -> Unit,
    onRemove: (NodeId) -> Unit,
) {
    var tab by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        InspectorHeader(
            spec = spec,
            node = node,
            canDelete = canDelete,
            onVariantChanged = onVariantChanged,
            onRemove = onRemove,
        )

        PrimaryTabRow(selectedTabIndex = tab) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text("Slots", style = MaterialTheme.typography.labelMedium) },
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text("Properties", style = MaterialTheme.typography.labelMedium) },
            )
        }

        when (tab) {
            0 -> SlotsPlaceholder()
            1 -> PropertiesTab(
                spec = spec,
                node = node,
                onPropertyChanged = onPropertyChanged,
            )
        }
    }
}

@Composable
private fun InspectorHeader(
    spec: ComponentSpec,
    node: AtomicNode,
    canDelete: Boolean,
    onVariantChanged: (NodeId, VariantId?) -> Unit,
    onRemove: (NodeId) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = spec.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (canDelete) {
                IconButton(
                    onClick = { onRemove(node.id) },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        if (spec.variants.isNotEmpty()) {
            VariantPicker(
                spec = spec,
                node = node,
                onVariantChanged = onVariantChanged,
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun VariantPicker(
    spec: ComponentSpec,
    node: AtomicNode,
    onVariantChanged: (NodeId, VariantId?) -> Unit,
) {
    val current = node.variant
    val currentLabel = spec.variants.firstOrNull { it.id == current }?.label
        ?: spec.variants.first().label
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Surface(
            onClick = { expanded = true },
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = currentLabel,
                style = MaterialTheme.typography.bodyMedium,
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
            spec.variants.forEach { variant ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = variant.label,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    onClick = {
                        onVariantChanged(node.id, variant.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun PropertiesTab(
    spec: ComponentSpec,
    node: AtomicNode,
    onPropertyChanged: (NodeId, PropertyKey, Value?) -> Unit,
) {
    if (spec.properties.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No editable properties",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        spec.properties.forEach { propertySpec ->
            val current = node.properties[propertySpec.key]
            PropertyEditor(
                spec = propertySpec,
                currentValue = current,
                onValueChange = { onPropertyChanged(node.id, propertySpec.key, it) },
            )
        }
    }
}

@Composable
private fun SlotsPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Slots view — coming next",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}