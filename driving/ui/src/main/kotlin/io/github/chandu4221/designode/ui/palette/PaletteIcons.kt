package io.github.chandu4221.designode.ui.palette

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material.icons.filled.WebAsset
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

internal fun iconFor(componentType: String): ImageVector = when (componentType) {
    "Text" -> Icons.Default.TextFields
    "Icon" -> Icons.Default.Star
    "Spacer" -> Icons.Default.SpaceBar
    "Row" -> Icons.Default.ViewAgenda
    "Column" -> Icons.Default.ViewColumn
    "Box" -> Icons.Default.CropSquare
    "Card" -> Icons.Default.CreditCard
    "Button" -> Icons.Default.SmartButton
    "FloatingActionButton" -> Icons.Default.Add
    "ExtendedFloatingActionButton" -> Icons.Default.AddCircle
    "TopAppBar" -> Icons.Default.WebAsset
    "NavigationBar" -> Icons.Default.Menu
    "NavigationBarItem" -> Icons.Default.Navigation
    else -> Icons.Default.Widgets
}