package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.codegen.emitters.IconEmitter
import io.github.chandu4221.designode.codegen.emitters.SpacerEmitter
import io.github.chandu4221.designode.codegen.emitters.TextEmitter
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.ModifierToken
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.service.CodeGenerator
import kotlin.test.Test
import kotlin.test.assertEquals

class AtomEmittersTest {

    private val textType = ComponentTypeId("Text")
    private val iconType = ComponentTypeId("Icon")
    private val spacerType = ComponentTypeId("Spacer")

    private val generator = CodeGenerator(
        mapOf(
            textType to TextEmitter(),
            iconType to IconEmitter(),
            spacerType to SpacerEmitter(),
        )
    )

    private fun text(
        content: String,
        style: String = "bodyMedium",
        color: Long = 0xFF000000L,
        align: String = "start",
        modifiers: List<ModifierToken> = emptyList(),
    ) = AtomicNode(
        id = NodeId.generate(),
        type = textType,
        properties = mapOf(
            PropertyKey("text") to Value.Text(content),
            PropertyKey("style") to Value.EnumValue(style),
            PropertyKey("color") to Value.Color(color),
            PropertyKey("textAlign") to Value.EnumValue(align),
        ),
        modifiers = modifiers,
    )

    private fun icon(
        name: String = "Favorite",
        description: String = "",
        tint: Long = 0xFF000000L,
        size: Float = 24f,
    ) = AtomicNode(
        id = NodeId.generate(),
        type = iconType,
        properties = mapOf(
            PropertyKey("name") to Value.Text(name),
            PropertyKey("contentDescription") to Value.Text(description),
            PropertyKey("tint") to Value.Color(tint),
            PropertyKey("size") to Value.Dp(size),
        ),
    )

    @Test
    fun `text emits with all properties`() {
        val output = generator.generate(
            text("Hello", style = "headlineLarge", color = 0xFFFF0000L, align = "center")
        )
        val expected = """
            Text(
                text = "Hello",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFFFF0000L),
                textAlign = TextAlign.Center,
            )
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `text escapes quotes`() {
        val output = generator.generate(text("Say \"hi\""))
        assertEquals(true, output.contains("\"Say \\\"hi\\\"\""))
    }

    @Test
    fun `text with modifier emits modifier arg`() {
        val output = generator.generate(
            text("Hi", modifiers = listOf(ModifierToken.Padding(8f)))
        )
        assertEquals(true, output.contains("modifier = Modifier.padding(8.0f.dp),"))
    }

    @Test
    fun `text with ColorRole emits MaterialTheme colorScheme reference`() {
        val node = text("Themed Text").copy(
            properties = mapOf(
                PropertyKey("text") to Value.Text("Themed Text"),
                PropertyKey("color") to Value.ColorRole("primary"),
            )
        )
        val output = generator.generate(node)
        assertEquals(true, output.contains("color = MaterialTheme.colorScheme.primary,"))
    }

    @Test
    fun `icon with ColorRole emits MaterialTheme colorScheme reference`() {
        val node = icon().copy(
            properties = mapOf(
                PropertyKey("name") to Value.Text("Favorite"),
                PropertyKey("tint") to Value.ColorRole("secondary"),
            )
        )
        val output = generator.generate(node)
        assertEquals(true, output.contains("tint = MaterialTheme.colorScheme.secondary,"))
    }

    @Test
    fun `icon emits with size modifier from property`() {
        val output = generator.generate(icon(name = "Star", size = 32f))
        val expected = """
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "",
                modifier = Modifier.size(32.0f.dp),
                tint = Color(0xFF000000L),
            )
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `icon prefers explicit modifier over size property`() {
        val node = icon(size = 32f).copy(
            modifiers = listOf(ModifierToken.Size(48f, 48f))
        )
        val output = generator.generate(node)
        assertEquals(true, output.contains("size(width = 48.0f.dp, height = 48.0f.dp)"))
        assertEquals(false, output.contains("size(32.0f.dp)"))
    }

    @Test
    fun `spacer emits bare modifier when none set`() {
        val node = AtomicNode(id = NodeId.generate(), type = spacerType)
        assertEquals("Spacer(modifier = Modifier)", generator.generate(node))
    }

    @Test
    fun `spacer emits size modifier chain`() {
        val node = AtomicNode(
            id = NodeId.generate(),
            type = spacerType,
            modifiers = listOf(ModifierToken.Size(16f, 16f)),
        )
        val output = generator.generate(node)
        assertEquals(
            "Spacer(modifier = Modifier.size(width = 16.0f.dp, height = 16.0f.dp))",
            output,
        )
    }

    @Test
    fun `spacer chains multiple modifiers in order`() {
        val node = AtomicNode(
            id = NodeId.generate(),
            type = spacerType,
            modifiers = listOf(
                ModifierToken.Padding(8f),
                ModifierToken.Size(16f, 16f),
            ),
        )
        val output = generator.generate(node)
        assertEquals(
            "Spacer(modifier = Modifier.padding(8.0f.dp).size(width = 16.0f.dp, height = 16.0f.dp))",
            output,
        )
    }
}