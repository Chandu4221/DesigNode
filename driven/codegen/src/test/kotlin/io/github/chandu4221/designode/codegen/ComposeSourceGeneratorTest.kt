package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.codegen.emitters.TextEmitter
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.service.CodeGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComposeSourceGeneratorTest {

    private val textType = ComponentTypeId("Text")

    private val wrapper = ComposeSourceGenerator(
        generator = CodeGenerator(mapOf(textType to TextEmitter()))
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

    @Test
    fun `output starts with import block`() {
        val output = wrapper.generate(text("Hi"))
        assertTrue(output.startsWith("import androidx.compose.foundation.layout.*"))
    }

    @Test
    fun `output contains opt-in and composable declaration`() {
        val output = wrapper.generate(text("Hi"))
        assertTrue(output.contains("@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun Screen() {"))
    }

    @Test
    fun `body is indented inside the function`() {
        val output = wrapper.generate(text("Hi"))
        assertTrue(output.contains("\n    Text(\n        text = \"Hi\","))
    }

    @Test
    fun `custom function name is used`() {
        val output = wrapper.generate(text("Hi"), functionName = "MyCustomScreen")
        assertTrue(output.contains("fun MyCustomScreen() {"))
    }

    @Test
    fun `escapes newline`() {
        val output = wrapper.generate(text("line1\nline2"))
        assertTrue(output.contains("text = \"line1\\nline2\""))
    }

    @Test
    fun `escapes tab`() {
        val output = wrapper.generate(text("col1\tcol2"))
        assertTrue(output.contains("text = \"col1\\tcol2\""))
    }

    @Test
    fun `escapes carriage return`() {
        val output = wrapper.generate(text("a\rb"))
        assertTrue(output.contains("text = \"a\\rb\""))
    }

    @Test
    fun `escapes dollar sign`() {
        val output = wrapper.generate(text("Price: \$100"))
        assertTrue(output.contains("text = \"Price: \\\$100\""))
    }

    @Test
    fun `escapes backslash`() {
        val output = wrapper.generate(text("a\\b"))
        assertTrue(output.contains("text = \"a\\\\b\""))
    }

    @Test
    fun `escapes double quote`() {
        val output = wrapper.generate(text("Say \"hi\""))
        assertTrue(output.contains("text = \"Say \\\"hi\\\"\""))
    }

    @Test
    fun `full output is compilable shape`() {
        val output = wrapper.generate(text("Hello"))
        val expected = """
            import androidx.compose.foundation.layout.*
            import androidx.compose.material.icons.Icons
            import androidx.compose.material.icons.filled.*
            import androidx.compose.material3.*
            import androidx.compose.material3.ExperimentalMaterial3Api
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.text.style.TextAlign
            import androidx.compose.ui.unit.dp

            @OptIn(ExperimentalMaterial3Api::class)
            @Composable
            fun Screen() {
                Text(
                    text = "Hello",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF000000L),
                    textAlign = TextAlign.Start,
                )
            }
        """.trimIndent()
        assertEquals(expected, output)
    }
}