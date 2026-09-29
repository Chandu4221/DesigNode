package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.spec.*
import kotlin.test.*

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
                cardinality = Cardinality.EXACTLY_ONE,
                accepts = setOf(textType, iconType),
                isDefault = true,
                required = true,
            )
        )
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
                cardinality = Cardinality.ZERO_OR_MANY,
                scope = LayoutScope.RowScope,
            )
        )
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
                SlotSpec(SlotId("content"), "Content", Cardinality.ZERO_OR_MANY,
                    scope = LayoutScope.BoxScope)
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
    fun `missingRequiredSlots reports unfilled required slot`() {
        val empty = node(buttonType)
        val missing = validator.missingRequiredSlots(empty)
        assertEquals(1, missing.size)
        assertEquals(SlotId("content"), missing.first().id)
    }

    @Test
    fun `missingRequiredSlots is empty when required slot filled`() {
        val filled = node(
            buttonType,
            slots = mapOf(SlotId("content") to SlotContent.One(node(textType))),
        )
        assertTrue(validator.missingRequiredSlots(filled).isEmpty())
    }
}