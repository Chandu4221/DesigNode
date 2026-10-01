package io.github.chandu4221.designode.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.materialkolor.PaletteStyle
import io.github.chandu4221.designode.domain.model.ThemeSpec

val PRESET_SEEDS = listOf(
    0xFF6750A4L to "Purple",
    0xFF0061A4L to "Blue",
    0xFF006A6AL to "Teal",
    0xFF386A20L to "Green",
    0xFF695F00L to "Yellow",
    0xFF8A5100L to "Amber",
    0xFF9C4146L to "Deep Orange",
    0xFFBA1A1AL to "Crimson",
    0xFF984061L to "Pink",
    0xFF5B5F66L to "Slate",
)

val PALETTE_STYLES = listOf(
    "TonalSpot" to "Tonal Spot (Default)",
    "Vibrant" to "Vibrant",
    "Expressive" to "Expressive",
    "Rainbow" to "Rainbow",
    "FruitSalad" to "Fruit Salad",
    "Content" to "Content",
    "Fidelity" to "Fidelity",
    "Neutral" to "Neutral",
    "Monochrome" to "Monochrome",
)

fun parsePaletteStyle(style: String): PaletteStyle = when (style) {
    "Vibrant" -> PaletteStyle.Vibrant
    "Expressive" -> PaletteStyle.Expressive
    "Rainbow" -> PaletteStyle.Rainbow
    "FruitSalad" -> PaletteStyle.FruitSalad
    "Content" -> PaletteStyle.Content
    "Fidelity" -> PaletteStyle.Fidelity
    "Neutral" -> PaletteStyle.Neutral
    "Monochrome" -> PaletteStyle.Monochrome
    else -> PaletteStyle.TonalSpot
}

@Composable
fun resolveColorRole(role: String, scheme: ColorScheme = MaterialTheme.colorScheme): Color = when (role) {
    "primary" -> scheme.primary
    "onPrimary" -> scheme.onPrimary
    "primaryContainer" -> scheme.primaryContainer
    "onPrimaryContainer" -> scheme.onPrimaryContainer
    "secondary" -> scheme.secondary
    "onSecondary" -> scheme.onSecondary
    "secondaryContainer" -> scheme.secondaryContainer
    "onSecondaryContainer" -> scheme.onSecondaryContainer
    "tertiary" -> scheme.tertiary
    "onTertiary" -> scheme.onTertiary
    "tertiaryContainer" -> scheme.tertiaryContainer
    "onTertiaryContainer" -> scheme.onTertiaryContainer
    "error" -> scheme.error
    "onError" -> scheme.onError
    "errorContainer" -> scheme.errorContainer
    "onErrorContainer" -> scheme.onErrorContainer
    "background" -> scheme.background
    "onBackground" -> scheme.onBackground
    "surface" -> scheme.surface
    "onSurface" -> scheme.onSurface
    "surfaceVariant" -> scheme.surfaceVariant
    "onSurfaceVariant" -> scheme.onSurfaceVariant
    "outline" -> scheme.outline
    "outlineVariant" -> scheme.outlineVariant
    else -> scheme.primary
}

@Composable
fun ThemeDialog(
    theme: ThemeSpec,
    onSeedColorChange: (Long) -> Unit,
    onDarkModeToggle: () -> Unit,
    onContrastChange: (Double) -> Unit,
    onStyleChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(440.dp)
                .wrapContentHeight()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Theme Settings",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // 1. Seed Color Swatches
                Text(
                    text = "Seed Color",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PRESET_SEEDS.forEach { (colorValue, name) ->
                        val isSelected = theme.seedColor == colorValue
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorValue))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.2f),
                                    shape = CircleShape,
                                )
                                .clickable { onSeedColorChange(colorValue) },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = name,
                                    tint = if (isColorDark(colorValue)) Color.White else Color.Black,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }

                // Custom Seed Hex Input
                var customHex by remember(theme.seedColor) {
                    mutableStateOf("#" + theme.seedColor.toULong().toString(16).uppercase().padStart(8, '0'))
                }
                OutlinedTextField(
                    value = customHex,
                    onValueChange = { input ->
                        customHex = input
                        val parsed = parseHexColor(input)
                        if (parsed != null) {
                            onSeedColorChange(parsed)
                        }
                    },
                    label = { Text("Custom Seed Hex", style = MaterialTheme.typography.labelSmall) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(theme.seedColor))
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // 2. Dark Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = if (theme.isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Column {
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = if (theme.isDark) "Active" else "Inactive",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Switch(
                        checked = theme.isDark,
                        onCheckedChange = { onDarkModeToggle() },
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // 3. Palette Style Dropdown
                Text(
                    text = "Palette Generation Style",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                var styleExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { styleExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = PALETTE_STYLES.firstOrNull { it.first == theme.style }?.second ?: theme.style,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    DropdownMenu(
                        expanded = styleExpanded,
                        onDismissRequest = { styleExpanded = false },
                    ) {
                        PALETTE_STYLES.forEach { (styleKey, label) ->
                            DropdownMenuItem(
                                text = { Text(label, style = MaterialTheme.typography.bodySmall) },
                                onClick = {
                                    onStyleChange(styleKey)
                                    styleExpanded = false
                                },
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // 4. Contrast Level Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Contrast Level",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${((theme.contrastLevel * 100).toInt())}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Slider(
                    value = theme.contrastLevel.toFloat(),
                    onValueChange = { onContrastChange(it.toDouble()) },
                    valueRange = -1.0f..1.0f,
                    steps = 8,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Done")
                }
            }
        }
    }
}

private fun isColorDark(colorValue: Long): Boolean {
    val r = ((colorValue shr 16) and 0xFF) / 255.0
    val g = ((colorValue shr 8) and 0xFF) / 255.0
    val b = (colorValue and 0xFF) / 255.0
    // Standard relative luminance
    val luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b
    return luminance < 0.5
}

private fun parseHexColor(input: String): Long? {
    val cleaned = input.removePrefix("#").trim()
    if (cleaned.length == 6) {
        val rgb = cleaned.toLongOrNull(16) ?: return null
        return 0xFF000000L or rgb
    }
    if (cleaned.length == 8) {
        return cleaned.toULongOrNull(16)?.toLong()
    }
    return null
}
