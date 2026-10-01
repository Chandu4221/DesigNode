package io.github.chandu4221.designode.ui.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class MaterialIconItem(
    val name: String,
    val vector: ImageVector,
    val category: String = "Common",
)

object MaterialIconRegistry {
    val allIcons: List<MaterialIconItem> = listOf(
        MaterialIconItem("Favorite", Icons.Default.Favorite, "Actions"),
        MaterialIconItem("Star", Icons.Default.Star, "Actions"),
        MaterialIconItem("Home", Icons.Default.Home, "Navigation"),
        MaterialIconItem("Settings", Icons.Default.Settings, "System"),
        MaterialIconItem("Search", Icons.Default.Search, "Navigation"),
        MaterialIconItem("Person", Icons.Default.Person, "Social"),
        MaterialIconItem("Email", Icons.Default.Email, "Communication"),
        MaterialIconItem("Phone", Icons.Default.Phone, "Communication"),
        MaterialIconItem("Share", Icons.Default.Share, "Social"),
        MaterialIconItem("Delete", Icons.Default.Delete, "Actions"),
        MaterialIconItem("Edit", Icons.Default.Edit, "Actions"),
        MaterialIconItem("Add", Icons.Default.Add, "Actions"),
        MaterialIconItem("Check", Icons.Default.Check, "Actions"),
        MaterialIconItem("Close", Icons.Default.Close, "Actions"),
        MaterialIconItem("ArrowBack", Icons.AutoMirrored.Filled.ArrowBack, "Navigation"),
        MaterialIconItem("ArrowForward", Icons.AutoMirrored.Filled.ArrowForward, "Navigation"),
        MaterialIconItem("Menu", Icons.Default.Menu, "Navigation"),
        MaterialIconItem("Notifications", Icons.Default.Notifications, "Alerts"),
        MaterialIconItem("ThumbUp", Icons.Default.ThumbUp, "Social"),
        MaterialIconItem("Lock", Icons.Default.Lock, "Security"),
        MaterialIconItem("Refresh", Icons.Default.Refresh, "Navigation"),
        MaterialIconItem("ShoppingCart", Icons.Default.ShoppingCart, "Commerce"),
        MaterialIconItem("PlayArrow", Icons.Default.PlayArrow, "Media"),
        MaterialIconItem("Info", Icons.Default.Info, "Alerts"),
        MaterialIconItem("Warning", Icons.Default.Warning, "Alerts"),
        MaterialIconItem("AccountCircle", Icons.Default.AccountCircle, "Social"),
        MaterialIconItem("Build", Icons.Default.Build, "Tools"),
        MaterialIconItem("Done", Icons.Default.Done, "Actions"),
        MaterialIconItem("Clear", Icons.Default.Clear, "Actions"),
        MaterialIconItem("Create", Icons.Default.Create, "Actions"),
        MaterialIconItem("Visibility", Icons.Default.Visibility, "System"),
        MaterialIconItem("Dashboard", Icons.Default.Dashboard, "Navigation"),
        MaterialIconItem("DateRange", Icons.Default.DateRange, "Content"),
        MaterialIconItem("Folder", Icons.Default.Folder, "Content"),
        MaterialIconItem("FolderOpen", Icons.Default.FolderOpen, "Content"),
        MaterialIconItem("Send", Icons.AutoMirrored.Filled.Send, "Communication"),
    )

    private val byName: Map<String, ImageVector> = allIcons.associate { it.name.lowercase() to it.vector }

    fun find(name: String): ImageVector? = byName[name.lowercase()]

    fun findOrDefault(name: String, default: ImageVector = Icons.Default.Favorite): ImageVector =
        byName[name.lowercase()] ?: default
}
