package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.codegen.emitters.ButtonEmitter
import io.github.chandu4221.designode.codegen.emitters.CardEmitter
import io.github.chandu4221.designode.codegen.emitters.TextEmitter
import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.service.CodeGenerator
import kotlin.test.Test
import kotlin.test.assertEquals

class ContainerEmittersTest {

    private val buttonType = ComponentTypeId("Button")
    private val cardType = ComponentTypeId("Card")
    private val textType = ComponentTypeId("Text")

    private val generator = CodeGenerator(
        mapOf(
            buttonType to ButtonEmitter(),
            cardType to CardEmitter(),
            textType to TextEmitter(),
        )
    )

    private fun text(content: String) = AtomicNode(
        id = NodeId.generate(),
        type = textType,
        properties = mapOf(
            PropertyKey("text") to Value.Text(content),
            PropertyKey("style") to Value.EnumValue("bodyMedium"),
            PropertyKey("color") to Value.Color(0xFF000000L),
            PropertyKey("textAlign") to Value.EnumValue("start"),
        ),
    )

    private fun button(
        variant: String? = null,
        enabled: Boolean = true,
        modifiers: List<ModifierToken> = emptyList(),
        children: List<AtomicNode> = listOf(text("Click")),
    ) = AtomicNode(
        id = NodeId.generate(),
        type = buttonType,
        variant = variant?.let { VariantId(it) },
        properties = mapOf(PropertyKey("enabled") to Value.Bool(enabled)),
        modifiers = modifiers,
        slots = if (children.isEmpty()) emptyMap()
        else mapOf(SlotId("content") to SlotContent.of(children)),
    )

    private fun card(
        variant: String? = null,
        modifiers: List<ModifierToken> = emptyList(),
        children: List<AtomicNode> = listOf(text("Body")),
    ) = AtomicNode(
        id = NodeId.generate(),
        type = cardType,
        variant = variant?.let { VariantId(it) },
        modifiers = modifiers,
        slots = if (children.isEmpty()) emptyMap()
        else mapOf(SlotId("content") to SlotContent.of(children)),
    )

    // ─────────────────────────────────────────────────────
    // Button
    // ─────────────────────────────────────────────────────

    @Test
    fun `filled button is the default when variant is null`() {
        val output = generator.generate(button())
        assertEquals(true, output.startsWith("Button("))
    }

    @Test
    fun `outlined variant emits OutlinedButton`() {
        val output = generator.generate(button(variant = "outlined"))
        assertEquals(true, output.startsWith("OutlinedButton("))
    }

    @Test
    fun `text variant emits TextButton`() {
        val output = generator.generate(button(variant = "text"))
        assertEquals(true, output.startsWith("TextButton("))
    }

    @Test
    fun `elevated variant emits ElevatedButton`() {
        val output = generator.generate(button(variant = "elevated"))
        assertEquals(true, output.startsWith("ElevatedButton("))
    }

    @Test
    fun `tonal variant emits FilledTonalButton`() {
        val output = generator.generate(button(variant = "tonal"))
        assertEquals(true, output.startsWith("FilledTonalButton("))
    }

    @Test
    fun `enabled button omits the enabled argument`() {
        val output = generator.generate(button(enabled = true))
        assertEquals(false, output.contains("enabled"))
    }

    @Test
    fun `disabled button emits enabled = false`() {
        val output = generator.generate(button(enabled = false))
        assertEquals(true, output.contains("enabled = false,"))
    }

    @Test
    fun `button emits onClick todo placeholder`() {
        val output = generator.generate(button())
        assertEquals(true, output.contains("onClick = { /* TODO */ },"))
    }

    @Test
    fun `button wraps child in trailing lambda`() {
        val output = generator.generate(button(children = listOf(text("Save"))))
        val expected = """
            Button(
                onClick = { /* TODO */ },
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF000000L),
                    textAlign = TextAlign.Start,
                )
            }
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `button with modifier emits modifier arg`() {
        val output = generator.generate(
            button(modifiers = listOf(ModifierToken.FillMaxWidth()))
        )
        assertEquals(true, output.contains("modifier = Modifier.fillMaxWidth(1.0f),"))
    }

    // ─────────────────────────────────────────────────────
    // Card
    // ─────────────────────────────────────────────────────

    @Test
    fun `filled card is the default when variant is null`() {
        val output = generator.generate(card())
        assertEquals(true, output.startsWith("Card {"))
    }

    @Test
    fun `elevated card emits ElevatedCard`() {
        val output = generator.generate(card(variant = "elevated"))
        assertEquals(true, output.startsWith("ElevatedCard {"))
    }

    @Test
    fun `outlined card emits OutlinedCard`() {
        val output = generator.generate(card(variant = "outlined"))
        assertEquals(true, output.startsWith("OutlinedCard {"))
    }

    @Test
    fun `card wraps child in trailing lambda`() {
        val output = generator.generate(card(children = listOf(text("Content"))))
        val expected = """
            Card {
                Text(
                    text = "Content",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF000000L),
                    textAlign = TextAlign.Start,
                )
            }
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `card with modifier emits argument list`() {
        val output = generator.generate(
            card(modifiers = listOf(ModifierToken.Padding(16f)))
        )
        val expected = """
            Card(
                modifier = Modifier.padding(16.0f.dp),
            ) {
                Text(
                    text = "Body",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF000000L),
                    textAlign = TextAlign.Start,
                )
            }
        """.trimIndent()
        assertEquals(expected, output)
    }
}