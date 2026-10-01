package io.github.chandu4221.designode.ui.inspector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 5-button visual arrangement segmented bar matching Gemini_Generated_Image_oo70jxoo70.png:
 * Top (|—), Bottom (—|), Center (—|—), Space Between (T), Space Evenly (|—|)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalArrangementBar(
    currentValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(
        ArrangementOption("top", "|—", "Top"),
        ArrangementOption("bottom", "—|", "Bottom"),
        ArrangementOption("center", "—|—", "Center"),
        ArrangementOption("spaceBetween", "T", "Space Between"),
        ArrangementOption("spaceEvenly", "|—|", "Space Evenly"),
    )

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text(
            text = "VerticalArrangement",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(8.dp),
                )
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            options.forEach { option ->
                val isSelected = option.key.equals(currentValue, ignoreCase = true)
                TooltipArea(
                    tooltip = {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.inverseSurface,
                            shadowElevation = 4.dp,
                        ) {
                            Text(
                                text = option.tooltip,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            )
                            .clickable { onValueChange(option.key) }
                            .semantics {
                                contentDescription = option.tooltip
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = option.symbol,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3-button visual horizontal alignment segmented bar:
 * Start (|—), Center (—|—), End (—|)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalAlignmentBar(
    currentValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(
        ArrangementOption("start", "|—", "Start"),
        ArrangementOption("centerHorizontally", "—|—", "Center"),
        ArrangementOption("end", "—|", "End"),
    )

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
        Text(
            text = "HorizontalAlignment",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(8.dp),
                )
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            options.forEach { option ->
                val isSelected = option.key.equals(currentValue, ignoreCase = true)
                TooltipArea(
                    tooltip = {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.inverseSurface,
                            shadowElevation = 4.dp,
                        ) {
                            Text(
                                text = option.tooltip,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            )
                            .clickable { onValueChange(option.key) }
                            .semantics {
                                contentDescription = option.tooltip
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = option.symbol,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private data class ArrangementOption(
    val key: String,
    val symbol: String,
    val tooltip: String,
)
