package io.github.chandu4221.designode.domain.service

import io.github.chandu4221.designode.domain.event.NodeEvent
import io.github.chandu4221.designode.domain.event.ProjectEvent
import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.port.NodeEventPublisher
import io.github.chandu4221.designode.domain.port.ProjectEventPublisher
import io.github.chandu4221.designode.domain.port.SequentialNodeIdGenerator
import io.github.chandu4221.designode.domain.spec.AtomicLevel
import io.github.chandu4221.designode.domain.spec.ComponentSpec
import io.github.chandu4221.designode.domain.spec.SlotSpec
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProjectSessionTest {

    // ─────────────────────────────────────────────────────
    // Fakes
    // ─────────────────────────────────────────────────────

    private val collected = mutableListOf<ProjectEvent>()

    private val projectPublisher = ProjectEventPublisher { event ->
        collected += event
    }

    private val publisher = NodeEventPublisher { _: NodeEvent ->
        // NodeTree-level events are ignored in this test suite.
    }

    // ─────────────────────────────────────────────────────
    // Registry setup
    // ─────────────────────────────────────────────────────

    private val rowType = ComponentTypeId("Row")

    private val rowSpec = ComponentSpec(
        type = rowType,
        family = FamilyId("Layout"),
        level = AtomicLevel.MOLECULE,
        label = "Row",
        slots = listOf(
            SlotSpec(
                id = SlotId("content"),
                label = "Content",
                cardinality = Cardinality.ZERO_OR_MANY,
            )
        ),
    )

    private val registry = InMemoryComponentRegistry(listOf(rowSpec))
    private val validator = SlotValidator(registry)

    // ─────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────

    private fun newSession(): ProjectSession = ProjectSession(
        projectId = ProjectId.generate(),
        registry = registry,
        validator = validator,
        publisher = publisher,
        projectPublisher = projectPublisher,
        idGenerator = SequentialNodeIdGenerator(),
    )

    private fun rootNode(): AtomicNode = AtomicNode(
        id = NodeId.generate(),
        type = rowType,
    )

    @BeforeTest
    fun setup() {
        collected.clear()
    }

    // ─────────────────────────────────────────────────────
    // Tests
    // ─────────────────────────────────────────────────────

    @Test
    fun `addScreen sets active and start`() {
        val session = newSession()
        val id = session.addScreen("Home", rootNode()).getOrThrow()

        assertEquals(id, session.activeScreenId)
        assertEquals(id, session.startScreen())
        assertEquals(1, session.screenIds().size)
    }

    @Test
    fun `addScreen emits ScreenAdded event`() {
        val session = newSession()
        session.addScreen("Home", rootNode()).getOrThrow()

        val added = collected.filterIsInstance<ProjectEvent.ScreenAdded>()
        assertEquals(1, added.size)
        assertEquals("Home", added.first().screen.name)
        assertEquals(0, added.first().index)
    }

    @Test
    fun `cannot remove last screen`() {
        val session = newSession()
        val id = session.addScreen("Home", rootNode()).getOrThrow()

        assertTrue(session.removeScreen(id).isFailure)
    }

    @Test
    fun `removeScreen switches active to first remaining`() {
        val session = newSession()
        val a = session.addScreen("A", rootNode()).getOrThrow()
        val b = session.addScreen("B", rootNode()).getOrThrow()

        session.switchScreen(b)
        session.removeScreen(b).getOrThrow()

        assertEquals(a, session.activeScreenId)
    }

    @Test
    fun `removeScreen emits ScreenRemoved event`() {
        val session = newSession()
        val a = session.addScreen("A", rootNode()).getOrThrow()
        session.addScreen("B", rootNode()).getOrThrow()

        session.removeScreen(a).getOrThrow()

        val removed = collected.filterIsInstance<ProjectEvent.ScreenRemoved>()
        assertEquals(1, removed.size)
        assertEquals(a, removed.first().screen.id)
        assertEquals(0, removed.first().index)
    }

    @Test
    fun `renameScreen changes name and emits event`() {
        val session = newSession()
        val id = session.addScreen("Old", rootNode()).getOrThrow()

        session.renameScreen(id, "New").getOrThrow()

        assertEquals("New", session.screenName(id))

        val renamed = collected.filterIsInstance<ProjectEvent.ScreenRenamed>()
        assertEquals(1, renamed.size)
        assertEquals("Old", renamed.first().oldName)
        assertEquals("New", renamed.first().newName)
    }

    @Test
    fun `switchScreen changes active tree`() {
        val session = newSession()
        val a = session.addScreen("A", rootNode()).getOrThrow()
        val b = session.addScreen("B", rootNode()).getOrThrow()

        session.switchScreen(b)
        assertEquals(b, session.activeScreenId)

        session.switchScreen(a)
        assertEquals(a, session.activeScreenId)
    }

    @Test
    fun `setStartScreen updates start and emits event`() {
        val session = newSession()
        val a = session.addScreen("A", rootNode()).getOrThrow()
        val b = session.addScreen("B", rootNode()).getOrThrow()

        assertEquals(a, session.startScreen())

        session.setStartScreen(b).getOrThrow()

        assertEquals(b, session.startScreen())
        val changed = collected.filterIsInstance<ProjectEvent.StartScreenChanged>()
        assertEquals(1, changed.size)
        assertEquals(a, changed.first().oldScreenId)
        assertEquals(b, changed.first().newScreenId)
    }

    @Test
    fun `snapshot round-trips through fromProject`() {
        val session = newSession()
        session.addScreen("Home", rootNode())
        session.rename("My Project")

        val project = session.snapshot()
        val restored = ProjectSession.fromProject(
            project = project,
            registry = registry,
            validator = validator,
            publisher = publisher,
            projectPublisher = projectPublisher,
            idGenerator = SequentialNodeIdGenerator(),
        )

        assertEquals(project.id.value, restored.projectId.value)
        assertEquals(project.name, restored.name)
        assertEquals(
            project.screens.keys.map { it.value }.toSet(),
            restored.screenIds().map { it.value }.toSet(),
        )
        assertEquals(project.startScreenId?.value, restored.startScreen()?.value)
    }
}