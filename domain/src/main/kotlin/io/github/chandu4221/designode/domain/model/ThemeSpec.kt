package io.github.chandu4221.designode.domain.model

data class ThemeSpec(
    val seedColor: Long = 0xFFD97746L, // Warm Terracotta / Coral seed color matching reference UI
    val isDark: Boolean = true,
    val contrastLevel: Double = 0.0,   // -1.0 to 1.0 (0.0 = standard M3 contrast)
    val style: String = "TonalSpot",  // PaletteStyle: TonalSpot, Vibrant, Expressive, etc.
) {
    companion object {
        val Default = ThemeSpec()
    }
}
