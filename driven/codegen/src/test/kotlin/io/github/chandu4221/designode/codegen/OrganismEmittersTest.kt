package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.codegen.emitters.IconEmitter
import io.github.chandu4221.designode.codegen.emitters.NavigationBarEmitter
import io.github.chandu4221.designode.codegen.emitters.NavigationBarItemEmitter
import io.github.chandu4221.designode.codegen.emitters.ScaffoldEmitter
import io.github.chandu4221.designode.codegen.emitters.TextEmitter
import io.github.chandu4221.designode.codegen.emitters.TopAppBarEmitter
import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.VariantId
import io.github.chandu4221.designode.domain.service.CodeGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrganismEmittersTest {

    private val scaffoldType = ComponentTypeId("Scaffold")
    private val topAppBarType = ComponentTypeId("TopAppBar")
    private val navBarType = ComponentTypeId("NavigationBar")
    private val navItemType = ComponentTypeId("NavigationBarItem")
    private val textType = ComponentTypeId("Text")
    private val iconType = ComponentTypeId("Icon")

    private val generator = CodeGenerator(
        mapOf(
            scaffoldType to ScaffoldEmitter(),
            topAppBarType to TopAppBarEmitter(),
            navBarType to NavigationBarEmitter(),
            navItemType to NavigationBarItemEmitter(),
            textType to TextEmitter(),
            iconType to IconEmitter(),
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

    private fun icon(name: String = "Home") = AtomicNode(
        id = NodeId.generate(),
        type = iconType,
        properties = mapOf(
            PropertyKey("name") to Value.Text(name),
            PropertyKey("contentDescription") to Value.Text(""),
            PropertyKey("tint") to Value.Color(0xFF000000L),
            PropertyKey("size") to Value.Dp(24f),
        ),
    )

    private fun topAppBar(
        variant: String? = null,
        title: AtomicNode? = text("Title"),
        navigationIcon: AtomicNode? = null,
        actions: List<AtomicNode> = emptyList(),
    ): AtomicNode {
        val slots = buildMap {
            title?.let { put(SlotId("title"), SlotContent.One(it)) }
            navigationIcon?.let { put(SlotId("navigationIcon"), SlotContent.One(it)) }
            if (actions.isNotEmpty()) put(SlotId("actions"), SlotContent.of(actions))
        }
        return AtomicNode(
            id = NodeId.generate(),
            type = topAppBarType,
            variant = variant?.let { VariantId(it) },
            slots = slots,
        )
    }

    private fun navItem(icon: AtomicNode, label: AtomicNode?): AtomicNode {
        val slots = buildMap {
            put(SlotId("icon"), SlotContent.One(icon))
            label?.let { put(SlotId("label"), SlotContent.One(it)) }
        }
        return AtomicNode(
            id = NodeId.generate(),
            type = navItemType,
            slots = slots,
        )
    }

    private fun navBar(items: List<AtomicNode>) = AtomicNode(
        id = NodeId.generate(),
        type = navBarType,
        slots = mapOf(SlotId("items") to SlotContent.of(items)),
    )

    private fun scaffold(
        topBar: AtomicNode? = null,
        bottomBar: AtomicNode? = null,
        fab: AtomicNode? = null,
        content: AtomicNode? = null,
    ): AtomicNode {
        val slots = buildMap {
            topBar?.let { put(SlotId("topBar"), SlotContent.One(it)) }
            bottomBar?.let { put(SlotId("bottomBar"), SlotContent.One(it)) }
            fab?.let { put(SlotId("floatingActionButton"), SlotContent.One(it)) }
            content?.let { put(SlotId("content"), SlotContent.One(it)) }
        }
        return AtomicNode(
            id = NodeId.generate(),
            type = scaffoldType,
            slots = slots,
        )
    }

    // ─────────────────────────────────────────────────────
    // TopAppBar
    // ─────────────────────────────────────────────────────

    @Test
    fun `small top app bar is the default`() {
        val output = generator.generate(topAppBar())
        assertTrue(output.startsWith("TopAppBar("))
    }

    @Test
    fun `medium variant emits MediumTopAppBar`() {
        val output = generator.generate(topAppBar(variant = "medium"))
        assertTrue(output.startsWith("MediumTopAppBar("))
    }

    @Test
    fun `large variant emits LargeTopAppBar`() {
        val output = generator.generate(topAppBar(variant = "large"))
        assertTrue(output.startsWith("LargeTopAppBar("))
    }

    @Test
    fun `centerAligned variant emits CenterAlignedTopAppBar`() {
        val output = generator.generate(topAppBar(variant = "centerAligned"))
        assertTrue(output.startsWith("CenterAlignedTopAppBar("))
    }

    @Test
    fun `top app bar renders title slot and skips empty slots`() {
        val output = generator.generate(topAppBar())
        assertTrue(output.contains("title = {"))
        assertTrue(output.contains("Text("))
        assertTrue(output.contains("navigationIcon = {},"))
        assertTrue(output.contains("actions = {},"))
    }

    @Test
    fun `top app bar with navigation icon`() {
        val output = generator.generate(topAppBar(navigationIcon = icon("Menu")))
        assertTrue(output.contains("navigationIcon = {"))
        assertTrue(output.contains("Icons.Default.Menu"))
    }

    @Test
    fun `top app bar with multiple actions`() {
        val output = generator.generate(
            topAppBar(actions = listOf(icon("Search"), icon("MoreVert")))
        )
        assertTrue(output.contains("Icons.Default.Search"))
        assertTrue(output.contains("Icons.Default.MoreVert"))
    }

    // ─────────────────────────────────────────────────────
    // NavigationBarItem
    // ─────────────────────────────────────────────────────

    @Test
    fun `nav item renders icon and label slots`() {
        val output = generator.generate(navItem(icon("Home"), text("Home")))
        assertTrue(output.contains("NavigationBarItem("))
        assertTrue(output.contains("selected = false,"))
        assertTrue(output.contains("onClick = { /* TODO */ },"))
        assertTrue(output.contains("icon = {"))
        assertTrue(output.contains("label = {"))
    }

    @Test
    fun `nav item without label emits empty label lambda`() {
        val output = generator.generate(navItem(icon("Home"), null))
        assertTrue(output.contains("label = {},"))
    }

    // ─────────────────────────────────────────────────────
    // NavigationBar
    // ─────────────────────────────────────────────────────

    @Test
    fun `nav bar wraps items in trailing lambda`() {
        val output = generator.generate(
            navBar(listOf(
                navItem(icon("Home"), text("Home")),
                navItem(icon("Search"), text("Search")),
                navItem(icon("Settings"), text("Settings")),
            ))
        )
        assertTrue(output.contains("NavigationBar {"))
        assertEquals(3, Regex("NavigationBarItem\\(").findAll(output).count())
    }

    // ─────────────────────────────────────────────────────
    // Scaffold
    // ─────────────────────────────────────────────────────

    @Test
    fun `scaffold with only content emits only content slot`() {
        val output = generator.generate(scaffold(content = text("Body")))
        assertTrue(output.contains("content = { padding ->"))
        assertTrue(!output.contains("topBar"))
        assertTrue(!output.contains("bottomBar"))
        assertTrue(!output.contains("floatingActionButton"))
    }

    @Test
    fun `scaffold with top bar and content`() {
        val output = generator.generate(
            scaffold(topBar = topAppBar(), content = text("Body"))
        )
        assertTrue(output.contains("topBar = {"))
        assertTrue(output.contains("TopAppBar("))
        assertTrue(output.contains("content = { padding ->"))
    }

    @Test
    fun `scaffold with bottom bar and content`() {
        val output = generator.generate(
            scaffold(
                bottomBar = navBar(listOf(
                    navItem(icon("Home"), text("Home")),
                    navItem(icon("Search"), text("Search")),
                    navItem(icon("Settings"), text("Settings")),
                )),
                content = text("Body"),
            )
        )
        assertTrue(output.contains("bottomBar = {"))
        assertTrue(output.contains("NavigationBar {"))
    }

    @Test
    fun `scaffold with all four slots nests correctly`() {
        val output = generator.generate(
            scaffold(
                topBar = topAppBar(),
                bottomBar = navBar(listOf(
                    navItem(icon("Home"), text("Home")),
                    navItem(icon("Search"), text("Search")),
                    navItem(icon("Settings"), text("Settings")),
                )),
                content = text("Body"),
            )
        )
        // Just verify order and presence; exact whitespace tested elsewhere
        val topBarIndex = output.indexOf("topBar = {")
        val bottomBarIndex = output.indexOf("bottomBar = {")
        val contentIndex = output.indexOf("content = { padding ->")
        assertTrue(topBarIndex < bottomBarIndex, "topBar should come before bottomBar")
        assertTrue(bottomBarIndex < contentIndex, "bottomBar should come before content")
    }
}