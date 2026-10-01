package io.github.chandu4221.designode.ui.canvas

import androidx.compose.ui.geometry.Rect
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.SlotId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HitTestRegistryTest {

    private val registry = HitTestRegistry()

    @Test
    fun `hitTest returns deepest matching node`() {
        val rootId = NodeId("root")
        val childId = NodeId("child")

        registry.record(rootId, Rect(0f, 0f, 360f, 720f))
        registry.record(childId, Rect(20f, 20f, 100f, 60f))

        // Point inside child and root -> child is smaller, should return child
        assertEquals(childId, registry.hitTest(50f, 40f))

        // Point inside root only -> returns root
        assertEquals(rootId, registry.hitTest(200f, 200f))

        // Point outside both -> returns null
        assertNull(registry.hitTest(400f, 800f))
    }

    @Test
    fun `hitTestSlot returns matching slot drop zone`() {
        val scaffoldId = NodeId("scaffold")
        val topBarSlot = SlotId("topBar")
        val bottomBarSlot = SlotId("bottomBar")

        registry.recordSlot(scaffoldId, topBarSlot, Rect(0f, 0f, 360f, 56f))
        registry.recordSlot(scaffoldId, bottomBarSlot, Rect(0f, 640f, 360f, 720f))

        val hitTop = registry.hitTestSlot(100f, 30f)
        assertEquals(SlotTarget(scaffoldId, topBarSlot), hitTop)

        val hitBottom = registry.hitTestSlot(100f, 680f)
        assertEquals(SlotTarget(scaffoldId, bottomBarSlot), hitBottom)

        // Point in between (content area)
        val hitMiddle = registry.hitTestSlot(100f, 300f)
        assertNull(hitMiddle)
    }

    @Test
    fun `clear empties both node and slot bounds`() {
        val scaffoldId = NodeId("scaffold")
        registry.record(scaffoldId, Rect(0f, 0f, 360f, 720f))
        registry.recordSlot(scaffoldId, SlotId("topBar"), Rect(0f, 0f, 360f, 56f))

        registry.clear()

        assertNull(registry.hitTest(100f, 30f))
        assertNull(registry.hitTestSlot(100f, 30f))
    }
}
