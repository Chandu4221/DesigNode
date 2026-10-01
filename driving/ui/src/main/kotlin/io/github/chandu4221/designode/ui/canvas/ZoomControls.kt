package io.github.chandu4221.designode.ui.canvas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ZoomControls(
    zoom: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onFitToScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Percentage + Fit Screen Pill
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
            modifier = Modifier.height(34.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "${(zoom * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onResetZoom)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
                IconButton(
                    onClick = onFitToScreen,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.FitScreen,
                        contentDescription = "Fit to screen",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Vertical +/- Stack Pill
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
            modifier = Modifier.width(34.dp),
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(
                    onClick = onZoomIn,
                    modifier = Modifier.size(28.dp),
                    enabled = zoom < 3.0f,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom in",
                        modifier = Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                androidx.compose.material3.HorizontalDivider(
                    modifier = Modifier.width(18.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                )

                IconButton(
                    onClick = onZoomOut,
                    modifier = Modifier.size(28.dp),
                    enabled = zoom > 0.25f,
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom out",
                        modifier = Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
