package io.github.chandu4221.designode.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class IdentityTest {

    @Test
    fun `NodeId rejects blank`() {
        assertFailsWith<IllegalArgumentException> { NodeId("") }
        assertFailsWith<IllegalArgumentException> { NodeId("   ") }
    }

    @Test
    fun `NodeId accepts non-blank`() {
        val id = NodeId("abc")
        assertEquals("abc", id.value)
    }

    @Test
    fun `NodeId equality is by value`() {
        assertEquals(NodeId("abc"), NodeId("abc"))
        assertNotEquals(NodeId("abc"), NodeId("xyz"))
    }

    @Test
    fun `NodeId generate produces unique values`() {
        val ids = (1..1000).map { NodeId.generate() }.toSet()
        assertEquals(1000, ids.size)
    }

    // ... same pattern for SlotId, ComponentTypeId, PropertyKey,
    //     VariantId, FamilyId, ProjectId, ScreenId
}