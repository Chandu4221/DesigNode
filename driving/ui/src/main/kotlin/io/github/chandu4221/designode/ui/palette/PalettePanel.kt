package io.github.chandu4221.designode.ui.palette

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.chandu4221.designode.domain.model.FamilyId
import io.github.chandu4221.designode.domain.spec.ComponentSpec

@Composable
fun PalettePanel(
    specs: List<ComponentSpec>,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var expandedFamilies by remember { mutableStateOf(specs.map { it.family }.toSet()) }

    val grouped = remember(query, specs) {
        specs
            .filter { query.isBlank() || it.label.contains(query, ignoreCase = true) }
            .groupBy { it.family }
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxHeight(),
    ) {
        Column(Modifier.fillMaxSize()) {
            PaletteHeader()
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            PaletteSearchField(
                query = query,
                onQueryChange = { query = it },
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                if (grouped.isEmpty()) {
                    item { EmptySearch(query) }
                } else {
                    grouped.forEach { (family, familySpecs) ->
                        val isExpanded = family in expandedFamilies
                        item(key = "family-${family.value}") {
                            FamilyHeader(
                                family = family,
                                count = familySpecs.size,
                                expanded = isExpanded,
                                onToggle = {
                                    expandedFamilies = if (isExpanded) {
                                        expandedFamilies - family
                                    } else {
                                        expandedFamilies + family
                                    }
                                },
                            )
                        }
                        if (isExpanded) {
                            items(familySpecs, key = { it.type.value }) { spec ->
                                PaletteItem(spec = spec)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaletteHeader() {
    Text(
        text = "COMPONENTS",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    )
}

@Composable
private fun PaletteSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text("Search", style = MaterialTheme.typography.bodySmall)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        textStyle = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun FamilyHeader(
    family: FamilyId,
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onToggle, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(18.dp)
                    .rotate(if (expanded) 0f else -90f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = family.value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f).padding(start = 4.dp),
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 12.dp),
        )
    }
}

@Composable
private fun EmptySearch(query: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No components match \"$query\"",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}