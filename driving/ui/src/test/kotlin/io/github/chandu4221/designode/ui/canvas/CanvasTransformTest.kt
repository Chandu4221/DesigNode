package io.github.chandu4221.designode.ui.canvas

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerKeyboardModifiers
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import androidx.compose.ui.unit.IntSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalComposeUiApi::class)
class CanvasTransformTest {

    @Test
    fun testPointerAndModifierExtensions() {
        val buttons = PointerButtons(0)
        assertFalse(buttons.isPrimaryPressed)
        assertFalse(buttons.isTertiaryPressed)

        val modifiers = PointerKeyboardModifiers(0)
        assertFalse(modifiers.isCtrlPressed)
        assertFalse(modifiers.isMetaPressed)
    }

    @Test
    fun testDefaultState() {
        val state = CanvasTransformState()
        assertEquals(1.0f, state.zoom)
        assertEquals(Offset.Zero, state.pan)
    }

    @Test
    fun testZoomInAndOut() {
        val state = CanvasTransformState(1.0f)
        state.zoomIn()
        assertEquals(1.25f, state.zoom)

        state.zoomOut()
        assertEquals(1.0f, state.zoom)

        state.zoomOut()
        assertEquals(0.75f, state.zoom)
    }

    @Test
    fun testZoomBoundsClamping() {
        val state = CanvasTransformState(0.25f)
        state.zoomOut()
        assertEquals(0.25f, state.zoom)

        val maxState = CanvasTransformState(3.0f)
        maxState.zoomIn()
        assertEquals(3.0f, maxState.zoom)

        maxState.updateZoom(5.0f)
        assertEquals(3.0f, maxState.zoom)

        maxState.updateZoom(0.1f)
        assertEquals(0.25f, maxState.zoom)
    }

    @Test
    fun testZoomByFactor() {
        val state = CanvasTransformState(1.0f)
        state.zoomByFactor(1.1f)
        assertEquals(1.1f, state.zoom)

        state.zoomByFactor(0.9f)
        assertEquals(0.99f, state.zoom)
    }

    @Test
    fun testPanByAndReset() {
        val state = CanvasTransformState()
        state.panBy(Offset(50f, -30f))
        assertEquals(Offset(50f, -30f), state.pan)

        state.panBy(Offset(10f, 20f))
        assertEquals(Offset(60f, -10f), state.pan)

        state.updateZoom(2.0f)
        state.resetZoom()
        assertEquals(1.0f, state.zoom)
        assertEquals(Offset.Zero, state.pan)
    }

    @Test
    fun testFitToScreen() {
        val state = CanvasTransformState()
        state.containerSize = IntSize(1000, 1000)
        state.panBy(Offset(100f, 100f))
        state.fitToScreen(contentWidth = 360f, contentHeight = 720f, padding = 64f)
        assertEquals(1.3f, state.zoom)
        assertEquals(Offset.Zero, state.pan)
    }
}
