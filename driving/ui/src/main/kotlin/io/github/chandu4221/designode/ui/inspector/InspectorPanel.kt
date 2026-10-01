package io.github.chandu4221.designode.ui.inspector

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec
import io.github.chandu4221.designode.ui.palette.iconFor

@Composable
fun InspectorPanel(
    selected: AtomicNode?,
    spec: ComponentSpec?,
    rootId: NodeId,
    specLookup: (ComponentTypeId) -> ComponentSpec?,
    onVariantChanged: (NodeId, VariantId?) -> Unit,
    onPropertyChanged: (NodeId, PropertyKey, Value?) -> Unit,
    onRemove: (NodeId) -> Unit,
    onNodeSelected: (NodeId?) -> Unit,
    onCopy: () -> Unit = {},
    onDuplicate: () -> Unit = {},
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
                specLookup = specLookup,
                onVariantChanged = onVariantChanged,
                onPropertyChanged = onPropertyChanged,
                onRemove = onRemove,
                onNodeSelected = onNodeSelected,
                onCopy = onCopy,
                onDuplicate = onDuplicate,
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
    specLookup: (ComponentTypeId) -> ComponentSpec?,
    onVariantChanged: (NodeId, VariantId?) -> Unit,
    onPropertyChanged: (NodeId, PropertyKey, Value?) -> Unit,
    onRemove: (NodeId) -> Unit,
    onNodeSelected: (NodeId?) -> Unit,
    onCopy: () -> Unit,
    onDuplicate: () -> Unit,
) {
    var tab by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        InspectorHeader(
            spec = spec,
            node = node,
            canDelete = canDelete,
            onVariantChanged = onVariantChanged,
            onRemove = onRemove,
            onCopy = onCopy,
            onDuplicate = onDuplicate,
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
            0 -> SlotsTab(
                spec = spec,
                node = node,
                specLookup = specLookup,
                onNodeSelected = onNodeSelected,
            )

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
    onCopy: () -> Unit,
    onDuplicate: () -> Unit,
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
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy (Ctrl+C)",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = onDuplicate,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CopyAll,
                        contentDescription = "Duplicate (Ctrl+D)",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = { onRemove(node.id) },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete (Del)",
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

// ─────────────────────────────────────────────────────
// Slots tab
// ─────────────────────────────────────────────────────

@Composable
private fun SlotsTab(
    spec: ComponentSpec,
    node: AtomicNode,
    specLookup: (ComponentTypeId) -> ComponentSpec?,
    onNodeSelected: (NodeId?) -> Unit,
) {
    if (spec.slots.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "This component has no slots",
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        spec.slots.forEach { slotSpec ->
            val children = node.slots[slotSpec.id]?.nodes().orEmpty()
            SlotSection(
                slotSpec = slotSpec,
                children = children,
                specLookup = specLookup,
                onChildClick = onNodeSelected,
            )
        }
    }
}

@Composable
private fun SlotSection(
    slotSpec: SlotSpec,
    children: List<AtomicNode>,
    specLookup: (ComponentTypeId) -> ComponentSpec?,
    onChildClick: (NodeId?) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = slotSpec.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = cardinalitySummary(slotSpec, children.size),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.padding(top = 4.dp))

        if (children.isEmpty()) {
            EmptySlotHint(slotSpec)
        } else {
            children.forEach { child ->
                SlotChildRow(
                    child = child,
                    label = specLookup(child.type)?.label ?: child.type.value,
                    onClick = { onChildClick(child.id) },
                )
            }
        }
    }
}

@Composable
private fun EmptySlotHint(slotSpec: SlotSpec) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Empty — drop a component here",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun SlotChildRow(
    child: AtomicNode,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = iconFor(child.type.value),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun cardinalitySummary(slot: SlotSpec, current: Int): String = when (val c = slot.cardinality) {
    Cardinality.ZeroOrOne -> "$current/1"
    Cardinality.ExactlyOne -> "$current/1"
    Cardinality.ZeroOrMany -> current.toString()
    Cardinality.OneOrMany -> "$current (min 1)"
    is Cardinality.Range -> "$current/${c.max}"
}