package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlotValidatorTest {

    private val buttonType = ComponentTypeId("Button")
    private val textType = ComponentTypeId("Text")
    private val iconType = ComponentTypeId("Icon")

    private val buttonSpec = ComponentSpec(
        type = buttonType,
        family = FamilyId("Actions"),
        level = AtomicLevel.ATOM,
        label = "Button",
        slots = listOf(
            SlotSpec(
                id = SlotId("content"),
                label = "Content",
                cardinality = Cardinality.ExactlyOne,
                accepts = setOf(textType, iconType),
                isDefault = true,
            )
        ),
    )

    private val rowSpec = ComponentSpec(
        type = ComponentTypeId("Row"),
        family = FamilyId("Layout"),
        level = AtomicLevel.MOLECULE,
        label = "Row",
        slots = listOf(
            SlotSpec(
                id = SlotId("content"),
                label = "Content",
                cardinality = Cardinality.ZeroOrMany,
                scope = LayoutScope.RowScope,
            )
        ),
    )

    private val registry = InMemoryComponentRegistry(listOf(buttonSpec, rowSpec))
    private val validator = SlotValidator(registry)

    private fun node(
        type: ComponentTypeId,
        modifiers: List<ModifierToken> = emptyList(),
        slots: Map<SlotId, SlotContent> = emptyMap(),
    ) = AtomicNode(
        id = NodeId.generate(),
        type = type,
        modifiers = modifiers,
        slots = slots,
    )

    @Test
    fun `accepts Text into Button content`() {
        val result = validator.canDrop(node(buttonType), SlotId("content"), node(textType))
        assertTrue(result.isSuccess)
    }

    @Test
    fun `rejects unknown slot`() {
        val result = validator.canDrop(node(buttonType), SlotId("nope"), node(textType))
        assertTrue(result.isFailure)
    }

    @Test
    fun `rejects disallowed type`() {
        val result = validator.canDrop(node(buttonType), SlotId("content"), node(buttonType))
        assertTrue(result.isFailure)
    }

    @Test
    fun `rejects second child in EXACTLY_ONE slot`() {
        val parent = node(
            buttonType,
            slots = mapOf(SlotId("content") to SlotContent.One(node(textType))),
        )
        val result = validator.canDrop(parent, SlotId("content"), node(textType))
        assertTrue(result.isFailure)
    }

    @Test
    fun `accepts multiple children in ZERO_OR_MANY slot`() {
        val parent = node(
            rowSpec.type,
            slots = mapOf(SlotId("content") to SlotContent.One(node(textType))),
        )
        val result = validator.canDrop(parent, SlotId("content"), node(textType))
        assertTrue(result.isSuccess)
    }

    @Test
    fun `rejects weight modifier in Box scope`() {
        val boxSpec = ComponentSpec(
            type = ComponentTypeId("Box"),
            family = FamilyId("Layout"),
            level = AtomicLevel.MOLECULE,
            label = "Box",
            slots = listOf(
                SlotSpec(
                    SlotId("content"), "Content", Cardinality.ZeroOrMany,
                    scope = LayoutScope.BoxScope
                )
            )
        )
        val reg = InMemoryComponentRegistry(listOf(boxSpec))
        val v = SlotValidator(reg)

        val child = node(textType, modifiers = listOf(ModifierToken.Weight(1f)))
        val result = v.canDrop(node(boxSpec.type), SlotId("content"), child)
        assertTrue(result.isFailure)
    }

    @Test
    fun `accepts weight modifier in Row scope`() {
        val child = node(textType, modifiers = listOf(ModifierToken.Weight(1f)))
        val result = validator.canDrop(node(rowSpec.type), SlotId("content"), child)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `accepts padding modifier in any scope`() {
        val child = node(textType, modifiers = listOf(ModifierToken.Padding(8f)))
        val result = validator.canDrop(node(buttonType), SlotId("content"), child)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `incompleteSlots reports unfilled ExactlyOne slot`() {
        val empty = node(buttonType)
        val missing = validator.incompleteSlots(empty)
        assertEquals(1, missing.size)
        assertEquals(SlotId("content"), missing.first().id)
    }

    @Test
    fun `incompleteSlots is empty when ExactlyOne slot filled`() {
        val filled = node(
            buttonType,
            slots = mapOf(SlotId("content") to SlotContent.One(node(textType))),
        )
        assertTrue(validator.incompleteSlots(filled).isEmpty())
    }

    @Test
    fun `incompleteSlots reports Range below min`() {
        val navBarSpec = ComponentSpec(
            type = ComponentTypeId("NavigationBar"),
            family = FamilyId("Bars"),
            level = AtomicLevel.ORGANISM,
            label = "Navigation bar",
            slots = listOf(
                SlotSpec(
                    id = SlotId("items"),
                    label = "Items",
                    cardinality = Cardinality.Range(min = 3, max = 5),
                )
            ),
        )
        val reg = InMemoryComponentRegistry(listOf(navBarSpec))
        val v = SlotValidator(reg)

        val partial = node(
            navBarSpec.type,
            slots = mapOf(
                SlotId("items") to SlotContent.of(listOf(node(textType), node(textType)))
            ),
        )
        val missing = v.incompleteSlots(partial)
        assertEquals(1, missing.size)
        assertEquals(SlotId("items"), missing.first().id)
    }

    @Test
    fun `canDrop rejects Range at max`() {
        val navBarSpec = ComponentSpec(
            type = ComponentTypeId("NavigationBar"),
            family = FamilyId("Bars"),
            level = AtomicLevel.ORGANISM,
            label = "Navigation bar",
            slots = listOf(
                SlotSpec(
                    id = SlotId("items"),
                    label = "Items",
                    cardinality = Cardinality.Range(min = 3, max = 5),
                )
            ),
        )
        val reg = InMemoryComponentRegistry(listOf(navBarSpec))
        val v = SlotValidator(reg)

        val full = node(
            navBarSpec.type,
            slots = mapOf(
                SlotId("items") to SlotContent.of(
                    List(5) { node(textType) }
                )
            ),
        )
        val result = v.canDrop(full, SlotId("items"), node(textType))
        assertTrue(result.isFailure)
    }
}