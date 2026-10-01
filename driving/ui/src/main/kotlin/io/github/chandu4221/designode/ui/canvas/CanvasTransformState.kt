package io.github.chandu4221.designode.ui.canvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Encapsulates zoom and pan state for the design canvas.
 */
class CanvasTransformState(
    initialZoom: Float = 1.0f,
    initialPan: Offset = Offset.Zero,
) {
    var zoom: Float by mutableStateOf(initialZoom.coerceIn(MIN_ZOOM, MAX_ZOOM))
        private set

    var pan: Offset by mutableStateOf(initialPan)
        private set

    var containerSize: IntSize by mutableStateOf(IntSize.Zero)

    fun zoomIn() {
        updateZoom(zoom + ZOOM_STEP)
    }

    fun zoomOut() {
        updateZoom(zoom - ZOOM_STEP)
    }

    fun zoomByFactor(factor: Float) {
        updateZoom(zoom * factor)
    }

    fun updateZoom(newZoom: Float) {
        val rounded = (newZoom * 100).roundToInt() / 100f
        zoom = rounded.coerceIn(MIN_ZOOM, MAX_ZOOM)
    }

    fun resetZoom() {
        zoom = 1.0f
        pan = Offset.Zero
    }

    fun panBy(delta: Offset) {
        pan += delta
    }

    fun fitToScreen(
        contentWidth: Float = 360f,
        contentHeight: Float = 720f,
        padding: Float = 64f,
    ) {
        if (containerSize.width <= 0 || containerSize.height <= 0) return
        val availableWidth = (containerSize.width - padding).coerceAtLeast(100f)
        val availableHeight = (containerSize.height - padding).coerceAtLeast(100f)
        val scaleX = availableWidth / contentWidth
        val scaleY = availableHeight / contentHeight
        val fitScale = min(scaleX, scaleY)
        updateZoom(fitScale)
        pan = Offset.Zero
    }

    companion object {
        const val MIN_ZOOM = 0.25f
        const val MAX_ZOOM = 3.0f
        const val ZOOM_STEP = 0.25f
    }
}

@Composable
fun rememberCanvasTransformState(
    initialZoom: Float = 1.0f,
    initialPan: Offset = Offset.Zero,
): CanvasTransformState = remember {
    CanvasTransformState(initialZoom, initialPan)
}
