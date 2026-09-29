package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.port.CodeEmitter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CodeGeneratorTest {

    private val textType = ComponentTypeId("Text")
    private val columnType = ComponentTypeId("Column")
    private val scaffoldType = ComponentTypeId("Scaffold")

    // Emits: Text("...")
    private val textEmitter = object : CodeEmitter {
        override fun emit(
            node: AtomicNode,
            children: Map<SlotId, List<String>>,
            indent: String,
        ): String {
            val text = (node.properties[PropertyKey("text")] as? Value.Text)?.value ?: ""
            return "${indent}Text(\"$text\")"
        }
    }

    // Emits: Column { <content> }
    private val columnEmitter = object : CodeEmitter {
        override fun emit(
            node: AtomicNode,
            children: Map<SlotId, List<String>>,
            indent: String,
        ): String {
            val body = children[SlotId("content")].orEmpty().joinToString("\n")
            return buildString {
                append("${indent}Column {")
                if (body.isNotEmpty()) {
                    append("\n").append(body)
                }
                append("\n${indent}}")
            }
        }
    }

    // Emits:
    // Scaffold(
    //     topBar = {
    //         <children, +1 extra level>
    //     },
    //     content = {
    //         <children, +1 extra level>
    //     },
    // )
    private val scaffoldEmitter = object : CodeEmitter {
        override fun emit(
            node: AtomicNode,
            children: Map<SlotId, List<String>>,
            indent: String,
        ): String {
            val shift = "    " // one extra indent level inside slot lambdas
            val inner = "$indent    " // where slot lambda bodies end
            val topBar = reindent(children[SlotId("topBar")].orEmpty(), shift)
            val content = reindent(children[SlotId("content")].orEmpty(), shift)

            return buildString {
                append("${indent}Scaffold(")
                append("\n${indent}    topBar = {")
                if (topBar.isNotEmpty()) append("\n").append(topBar)
                append("\n${indent}    },")
                append("\n${indent}    content = {")
                if (content.isNotEmpty()) append("\n").append(content)
                append("\n${indent}    },")
                append("\n${indent})")
            }
        }

        /**
         * Prefixes each non-blank line of [lines] with [shift] extra spaces.
         * This lets an emitter whose structure opens extra nesting levels
         * (like slot lambdas) push its children deeper than the generator's
         * default one-level-per-node rule.
         */
        private fun reindent(lines: List<String>, shift: String): String =
            lines.joinToString("\n") { source ->
                source.lines().joinToString("\n") { line ->
                    if (line.isBlank()) line else "$shift$line"
                }
            }
    }

    private val generator = CodeGenerator(
        mapOf(
            textType to textEmitter,
            columnType to columnEmitter,
            scaffoldType to scaffoldEmitter,
        )
    )

    private fun text(content: String): AtomicNode = AtomicNode(
        id = NodeId.generate(),
        type = textType,
        properties = mapOf(PropertyKey("text") to Value.Text(content)),
    )

    private fun column(vararg children: AtomicNode): AtomicNode = AtomicNode(
        id = NodeId.generate(),
        type = columnType,
        slots = mapOf(SlotId("content") to SlotContent.of(children.toList())),
    )

    private fun scaffold(
        topBar: AtomicNode? = null,
        content: AtomicNode? = null,
    ): AtomicNode {
        val slots = buildMap {
            if (topBar != null) put(SlotId("topBar"), SlotContent.One(topBar))
            if (content != null) put(SlotId("content"), SlotContent.One(content))
        }
        return AtomicNode(
            id = NodeId.generate(),
            type = scaffoldType,
            slots = slots,
        )
    }

    // ─────────────────────────────────────────────────────
    // Tests
    // ─────────────────────────────────────────────────────

    @Test
    fun `single node with no children`() {
        val output = generator.generate(text("Hello"))
        assertEquals("Text(\"Hello\")", output)
    }

    @Test
    fun `column with one child indents the child`() {
        val tree = column(text("Hi"))
        val output = generator.generate(tree)

        val expected = """
            Column {
                Text("Hi")
            }
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `column with multiple children`() {
        val tree = column(text("A"), text("B"))
        val output = generator.generate(tree)

        val expected = """
            Column {
                Text("A")
                Text("B")
            }
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `scaffold with topBar and content`() {
        val tree = scaffold(
            topBar = text("Title"),
            content = column(text("Body")),
        )
        val output = generator.generate(tree)

        val expected = """
            Scaffold(
                topBar = {
                    Text("Title")
                },
                content = {
                    Column {
                        Text("Body")
                    }
                },
            )
        """.trimIndent()
        assertEquals(expected, output)
    }

    @Test
    fun `canGenerate returns true for known types`() {
        val tree = scaffold(content = column(text("Hi")))
        assertTrue(generator.canGenerate(tree))
    }

    @Test
    fun `canGenerate returns false for unknown type`() {
        val unknown = AtomicNode(
            id = NodeId.generate(),
            type = ComponentTypeId("Unknown"),
        )
        assertFalse(generator.canGenerate(unknown))
    }

    @Test
    fun `generate throws on unknown type`() {
        val unknown = AtomicNode(
            id = NodeId.generate(),
            type = ComponentTypeId("Unknown"),
        )
        assertFailsWith<IllegalStateException> {
            generator.generate(unknown)
        }
    }

    @Test
    fun `deeply nested tree renders with correct indentation`() {
        val tree = column(
            column(
                text("deep")
            )
        )
        val output = generator.generate(tree)

        val expected = """
            Column {
                Column {
                    Text("deep")
                }
            }
        """.trimIndent()
        assertEquals(expected, output)
    }
}