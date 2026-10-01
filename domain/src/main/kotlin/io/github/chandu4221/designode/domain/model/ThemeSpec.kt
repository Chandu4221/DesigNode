package io.github.chandu4221.designode.domain.model

data class ThemeSpec(
    val seedColor: Long = 0xFF6750A4L, // Default Material 3 Purple seed
    val isDark: Boolean = false,
    val contrastLevel: Double = 0.0,   // -1.0 to 1.0 (0.0 = standard M3 contrast)
    val style: String = "TonalSpot",  // PaletteStyle: TonalSpot, Vibrant, Expressive, etc.
) {
    companion object {
        val Default = ThemeSpec()
    }
}
