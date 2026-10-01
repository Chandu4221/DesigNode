package io.github.chandu4221.designode.ui.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    rootNode: AtomicNode? = null,
    onModifiersChanged: (NodeId, List<ModifierToken>) -> Unit = { _, _ -> },
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
                rootNode = rootNode,
                onModifiersChanged = onModifiersChanged,
            )
        }
    }
}

@Composable
private fun EmptyInspector() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Select a component",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Click any element on canvas or tree to inspect its properties",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
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
    rootNode: AtomicNode?,
    onModifiersChanged: (NodeId, List<ModifierToken>) -> Unit,
) {
    var tab by remember { mutableStateOf(1) } // Default to Properties tab matching workflow

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

        PrimaryTabRow(
            selectedTabIndex = tab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
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
                selectedNode = node,
                rootNode = rootNode ?: node,
                specLookup = specLookup,
                onNodeSelected = onNodeSelected,
            )

            1 -> PropertiesTab(
                spec = spec,
                node = node,
                onPropertyChanged = onPropertyChanged,
                onModifiersChanged = onModifiersChanged,
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
    Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
        // Top label "INSPECTOR"
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "INSPECTOR",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Component Icon + Label + Actions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = iconFor(node.type.value),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(8.dp))
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
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy (Ctrl+C)",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = onDuplicate,
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CopyAll,
                        contentDescription = "Duplicate (Ctrl+D)",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = { onRemove(node.id) },
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete (Del)",
                        modifier = Modifier.size(16.dp),
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
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
    onModifiersChanged: (NodeId, List<ModifierToken>) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Modifier section matching the mockup
        ModifierSection(
            modifiers = node.modifiers,
            onModifiersChanged = { onModifiersChanged(node.id, it) },
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )

        if (spec.properties.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "No additional properties",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
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
}

// ─────────────────────────────────────────────────────
// Slots tab / Tree Outline View matching reference design
// ─────────────────────────────────────────────────────

@Composable
private fun SlotsTab(
    spec: ComponentSpec,
    selectedNode: AtomicNode,
    rootNode: AtomicNode,
    specLookup: (ComponentTypeId) -> ComponentSpec?,
    onNodeSelected: (NodeId?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Render tree outline from root
        TreeOutlineNode(
            node = rootNode,
            selectedId = selectedNode.id,
            depth = 0,
            specLookup = specLookup,
            onNodeSelected = onNodeSelected,
        )
    }
}

@Composable
private fun TreeOutlineNode(
    node: AtomicNode,
    selectedId: NodeId,
    depth: Int,
    specLookup: (ComponentTypeId) -> ComponentSpec?,
    onNodeSelected: (NodeId?) -> Unit,
) {
    val isSelected = node.id == selectedId
    val label = specLookup(node.type)?.label ?: node.type.value
    var expanded by remember { mutableStateOf(true) }

    val allChildren = remember(node) {
        node.slots.values.flatMap { it.nodes() }
    }
    val hasChildren = allChildren.isNotEmpty()

    Surface(
        onClick = { onNodeSelected(node.id) },
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (depth * 14).dp, top = 2.dp, bottom = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (hasChildren) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { expanded = !expanded },
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Spacer(Modifier.width(16.dp))
            }

            Spacer(Modifier.width(4.dp))

            Icon(
                imageVector = iconFor(node.type.value),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }

    if (hasChildren && expanded) {
        allChildren.forEach { child ->
            TreeOutlineNode(
                node = child,
                selectedId = selectedId,
                depth = depth + 1,
                specLookup = specLookup,
                onNodeSelected = onNodeSelected,
            )
        }
    }
}