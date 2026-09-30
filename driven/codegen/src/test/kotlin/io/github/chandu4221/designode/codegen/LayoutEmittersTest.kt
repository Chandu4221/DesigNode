package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.codegen.emitters.BoxEmitter
import io.github.chandu4221.designode.codegen.emitters.ColumnEmitter
import io.github.chandu4221.designode.codegen.emitters.RowEmitter
import io.github.chandu4221.designode.codegen.emitters.TextEmitter
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.ModifierToken
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.service.CodeGenerator
import kotlin.test.Test
import kotlin.test.assertEquals

class LayoutEmittersTest {

    private val rowType = ComponentTypeId("Row")
    private val columnType = ComponentTypeId("Column")
    private val boxType = ComponentTypeId("Box")
    private val textType = ComponentTypeId("Text")

    private val generator = CodeGenerator(
        mapOf(
            rowType to RowEmitter(),
            columnType to ColumnEmitter(),
            boxType to BoxEmitter(),
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

    private fun row(
        arrangement: String = "start",
        alignment: String = "top",
        modifiers: List<ModifierToken> = emptyList(),
        children: List<AtomicNode> = emptyList(),
    ) = AtomicNode(
        id = NodeId.generate(),
        type = rowType,
        properties = mapOf(
            PropertyKey("horizontalArrangement") to Value.EnumValue(arrangement),
            PropertyKey("verticalAlignment") to Value.EnumValue(alignment),
        ),
        modifiers = modifiers,
        slots = if (children.isEmpty()) emptyMap()
        else mapOf(SlotId("content") to SlotContent.of(children)),
    )

    private fun column(
        arrangement: String = "top",
        alignment: String = "start",
        children: List<AtomicNode> = emptyList(),
    ) = AtomicNode(
        id = NodeId.generate(),
        type = columnType,
        properties = mapOf(
            PropertyKey("verticalArrangement") to Value.EnumValue(arrangement),
            PropertyKey("horizontalAlignment") to Value.EnumValue(alignment),
        ),
        slots = if (children.isEmpty()) emptyMap()
        else mapOf(SlotId("content") to SlotContent.of(children)),
    )

    private fun box(
        alignment: String = "topStart",
        children: List<AtomicNode> = emptyList(),
    ) = AtomicNode(
        id = NodeId.generate(),
        type = boxType,
        properties = mapOf(
            PropertyKey("contentAlignment") to Value.EnumValue(alignment),
        ),
        slots = if (children.isEmpty()) emptyMap()
        else mapOf(SlotId("content") to SlotContent.of(children)),
    )

    @Test
    fun `empty row emits bare block`() {
        val output = generator.generate(row())
        val expected = """
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
            ) {}
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `row with one child nests it`() {
        val output = generator.generate(row(children = listOf(text("Hi"))))
        val expected = """
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "Hi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF000000L),
                    textAlign = TextAlign.Start,
                )
            }
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `row with spaceBetween arrangement`() {
        val output = generator.generate(row(arrangement = "spaceBetween"))
        assertEquals(true, output.contains("horizontalArrangement = Arrangement.SpaceBetween"))
    }

    @Test
    fun `row with modifier emits modifier arg`() {
        val output = generator.generate(
            row(modifiers = listOf(ModifierToken.FillMaxWidth()))
        )
        assertEquals(true, output.contains("modifier = Modifier.fillMaxWidth(1.0f),"))
    }

    @Test
    fun `column with two children`() {
        val output = generator.generate(
            column(children = listOf(text("A"), text("B")))
        )
        val expected = """
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF000000L),
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = "B",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF000000L),
                    textAlign = TextAlign.Start,
                )
            }
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `column with centered alignment`() {
        val output = generator.generate(
            column(arrangement = "center", alignment = "centerHorizontally")
        )
        assertEquals(true, output.contains("verticalArrangement = Arrangement.Center"))
        assertEquals(true, output.contains("horizontalAlignment = Alignment.CenterHorizontally"))
    }

    @Test
    fun `box with child`() {
        val output = generator.generate(box(children = listOf(text("Hi"))))
        val expected = """
            Box(
                contentAlignment = Alignment.TopStart,
            ) {
                Text(
                    text = "Hi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF000000L),
                    textAlign = TextAlign.Start,
                )
            }
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `box with center alignment`() {
        val output = generator.generate(box(alignment = "center"))
        assertEquals(true, output.contains("contentAlignment = Alignment.Center"))
    }

    @Test
    fun `nested column inside row indents both levels`() {
        val tree = row(
            children = listOf(
                column(children = listOf(text("nested")))
            )
        )
        val output = generator.generate(tree)
        val expected = """
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "nested",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF000000L),
                        textAlign = TextAlign.Start,
                    )
                }
            }
        """.trimIndent()
        assertEquals(expected, output)
    }
}