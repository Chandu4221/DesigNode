package io.github.chandu4221.designode.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class ActivityBarItem {
    EXPLORER,
    CANVAS,
    COMPONENTS,
    THEME,
}

@Composable
fun ActivityBar(
    activeItem: ActivityBarItem = ActivityBarItem.EXPLORER,
    onItemSelect: (ActivityBarItem) -> Unit = {},
    onOpenTheme: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier
            .width(48.dp)
            .fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Group
            ActivityBarIcon(
                icon = Icons.Default.Folder,
                contentDescription = "Project Explorer",
                isSelected = activeItem == ActivityBarItem.EXPLORER,
                onClick = { onItemSelect(ActivityBarItem.EXPLORER) },
            )
            Spacer(Modifier.height(4.dp))
            ActivityBarIcon(
                icon = Icons.Default.GridView,
                contentDescription = "Canvas Layout",
                isSelected = activeItem == ActivityBarItem.CANVAS,
                onClick = { onItemSelect(ActivityBarItem.CANVAS) },
            )
            Spacer(Modifier.height(4.dp))
            ActivityBarIcon(
                icon = Icons.Default.TextFields,
                contentDescription = "Components & Typography",
                isSelected = activeItem == ActivityBarItem.COMPONENTS,
                onClick = { onItemSelect(ActivityBarItem.COMPONENTS) },
            )
            Spacer(Modifier.height(4.dp))
            ActivityBarIcon(
                icon = Icons.Default.Palette,
                contentDescription = "Theme & Colors",
                isSelected = activeItem == ActivityBarItem.THEME,
                onClick = {
                    onItemSelect(ActivityBarItem.THEME)
                    onOpenTheme()
                },
            )

            Spacer(Modifier.weight(1f))

            // Bottom Group
            ActivityBarIcon(
                icon = Icons.Default.Edit,
                contentDescription = "Vector & Tools",
                isSelected = false,
                onClick = {},
            )
            Spacer(Modifier.height(4.dp))
            ActivityBarIcon(
                icon = Icons.Default.Notifications,
                contentDescription = "Notifications",
                isSelected = false,
                onClick = {},
            )
            Spacer(Modifier.height(4.dp))
            ActivityBarIcon(
                icon = Icons.Default.Info,
                contentDescription = "Help & Docs",
                isSelected = false,
                onClick = {},
            )
            Spacer(Modifier.height(4.dp))
            ActivityBarIcon(
                icon = Icons.Default.AccountTree,
                contentDescription = "Git & Branches",
                isSelected = false,
                onClick = {},
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ActivityBarIcon(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val indicatorColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    TooltipArea(
        tooltip = {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                shadowElevation = 4.dp,
            ) {
                Text(
                    text = contentDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(3.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                        .background(indicatorColor)
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
