package io.github.chandu4221.designode.codegen

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmitterRegistryTest {

    @Test
    fun `registry has one emitter per component type`() {
        val emitters = EmitterRegistry.emitters
        assertEquals(14, emitters.size)
    }

    @Test
    fun `every expected component type is registered`() {
        val expected = setOf(
            "Text", "Icon", "Spacer",
            "Row", "Column", "Box", "Scaffold",
            "Card",
            "Button", "FloatingActionButton", "ExtendedFloatingActionButton",
            "TopAppBar", "NavigationBar", "NavigationBarItem",
        )
        val actual = EmitterRegistry.emitters.keys.map { it.value }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun `registry produces a usable code generator`() {
        val generator = EmitterRegistry.codeGenerator()
        assertTrue(generator.canGenerate(
            io.github.chandu4221.designode.domain.model.AtomicNode(
                id = io.github.chandu4221.designode.domain.model.NodeId.generate(),
                type = io.github.chandu4221.designode.domain.model.ComponentTypeId("Text"),
            )
        ))
    }
}