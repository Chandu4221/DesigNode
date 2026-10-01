package io.github.chandu4221.designode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusBar(
    saveStatus: SaveStatus,
    branchName: String = "main",
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier
            .fillMaxWidth()
            .height(26.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left Status: Git & Sync
            Icon(
                imageVector = Icons.Default.AccountTree,
                contentDescription = "Git branch",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(13.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "git: $branchName",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "|",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Spacer(Modifier.width(8.dp))

            val statusText = when (saveStatus) {
                SaveStatus.Idle -> "No changes"
                SaveStatus.Saving -> "Saving…"
                SaveStatus.Saved -> "Saved"
                is SaveStatus.Failed -> "Save error: ${saveStatus.message}"
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                color = when (saveStatus) {
                    is SaveStatus.Failed -> MaterialTheme.colorScheme.error
                    SaveStatus.Saving -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                },
            )

            Spacer(Modifier.weight(1f))

            // Right Status: Tools & Build
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Assist",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                modifier = Modifier.size(13.dp),
            )
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Warnings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(13.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "No changes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Build status: OK",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }
    }
}
