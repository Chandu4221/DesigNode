package io.github.chandu4221.designode.ui.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MaterialIconRegistryTest {

    @Test
    fun `finds existing icon by exact name`() {
        val icon = MaterialIconRegistry.find("Favorite")
        assertNotNull(icon)
        assertEquals(Icons.Default.Favorite, icon)
    }

    @Test
    fun `finds existing icon case-insensitively`() {
        val icon = MaterialIconRegistry.find("favorite")
        assertNotNull(icon)
        assertEquals(Icons.Default.Favorite, icon)

        val star = MaterialIconRegistry.find("STAR")
        assertNotNull(star)
        assertEquals(Icons.Default.Star, star)
    }

    @Test
    fun `returns null for unknown icon`() {
        val icon = MaterialIconRegistry.find("UnknownNonexistentIcon123")
        assertNull(icon)
    }

    @Test
    fun `findOrDefault returns fallback for unknown icon`() {
        val fallback = MaterialIconRegistry.findOrDefault("UnknownNonexistentIcon123", Icons.Default.Star)
        assertEquals(Icons.Default.Star, fallback)
    }

    @Test
    fun `registry contains common app icons`() {
        assertTrue(MaterialIconRegistry.allIcons.size >= 30)
        assertNotNull(MaterialIconRegistry.find("Settings"))
        assertNotNull(MaterialIconRegistry.find("Home"))
        assertNotNull(MaterialIconRegistry.find("Search"))
        assertNotNull(MaterialIconRegistry.find("Delete"))
        assertNotNull(MaterialIconRegistry.find("Edit"))
    }

    private fun assertTrue(condition: Boolean) {
        kotlin.test.assertTrue(condition)
    }
}
