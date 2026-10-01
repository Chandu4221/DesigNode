package io.github.chandu4221.designode.persistence

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.NodeId
import io.github.chandu4221.designode.domain.model.Project
import io.github.chandu4221.designode.domain.model.ProjectId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.Screen
import io.github.chandu4221.designode.domain.model.ScreenId
import io.github.chandu4221.designode.domain.model.SlotContent
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.model.nodes
import kotlinx.coroutines.test.runTest
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsonProjectRepositoryTest {

    private lateinit var tempDir: Path
    private lateinit var repo: JsonProjectRepository

    @BeforeTest
    fun setup() {
        tempDir = createTempDirectory("designode-test-")
        repo = JsonProjectRepository(tempDir)
    }

    @OptIn(ExperimentalPathApi::class)
    @AfterTest
    fun cleanup() {
        tempDir.deleteRecursively()
    }

    private fun sampleProject(): Project {
        val text = AtomicNode(
            id = NodeId("text-1"),
            type = ComponentTypeId("Text"),
            properties = mapOf(
                PropertyKey("text") to Value.Text("Hello"),
                PropertyKey("style") to Value.EnumValue("bodyMedium"),
            ),
        )
        val root = AtomicNode(
            id = NodeId("root-1"),
            type = ComponentTypeId("Column"),
            slots = mapOf(SlotId("content") to SlotContent.One(text)),
        )
        val screen = Screen(
            id = ScreenId("screen-1"),
            name = "Home",
            root = root,
        )
        return Project(
            id = ProjectId("project-1"),
            name = "Test Project",
            screens = mapOf(screen.id to screen),
            startScreenId = screen.id,
        )
    }

    @Test
    fun `save then load round-trips project metadata`() = runTest {
        val original = sampleProject()
        repo.save(original).getOrThrow()
        val loaded = repo.load(original.id).getOrThrow()

        assertEquals(original.id.value, loaded.id.value)
        assertEquals(original.name, loaded.name)
        assertEquals(original.startScreenId?.value, loaded.startScreenId?.value)
        assertEquals(original.screens.keys.map { it.value }, loaded.screens.keys.map { it.value })
    }

    @Test
    fun `round-trip preserves node tree structure`() = runTest {
        val original = sampleProject()
        repo.save(original).getOrThrow()
        val loaded = repo.load(original.id).getOrThrow()

        val originalRoot = original.screens.values.first().root
        val loadedRoot = loaded.screens.values.first().root

        assertEquals(originalRoot.id.value, loadedRoot.id.value)
        assertEquals(originalRoot.type.value, loadedRoot.type.value)

        val originalChildren = originalRoot.slots[SlotId("content")]?.nodes().orEmpty()
        val loadedChildren = loadedRoot.slots[SlotId("content")]?.nodes().orEmpty()

        assertEquals(1, loadedChildren.size)
        assertEquals("text-1", loadedChildren[0].id.value)
        assertEquals(Value.Text("Hello"), loadedChildren[0].properties[PropertyKey("text")])
        assertEquals(Value.EnumValue("bodyMedium"), loadedChildren[0].properties[PropertyKey("style")])
    }

    @Test
    fun `list returns saved projects sorted by name`() = runTest {
        repo.save(sampleProject()).getOrThrow()
        val second = sampleProject().let {
            it.copy(id = ProjectId("project-2"), name = "Alpha Project")
        }
        repo.save(second).getOrThrow()

        val summaries = repo.list().getOrThrow()
        assertEquals(2, summaries.size)
        assertEquals("Alpha Project", summaries[0].name)
        assertEquals("Test Project", summaries[1].name)
    }

    @Test
    fun `list summary includes screen count`() = runTest {
        repo.save(sampleProject()).getOrThrow()
        val summaries = repo.list().getOrThrow()
        assertEquals(1, summaries.first().screenCount)
    }

    @Test
    fun `list returns empty when no projects exist`() = runTest {
        assertTrue(repo.list().getOrThrow().isEmpty())
    }

    @Test
    fun `delete removes a project`() = runTest {
        val project = sampleProject()
        repo.save(project).getOrThrow()
        repo.delete(project.id).getOrThrow()
        assertTrue(repo.load(project.id).isFailure)
    }

    @Test
    fun `delete of nonexistent project succeeds`() = runTest {
        repo.delete(ProjectId("ghost")).getOrThrow()
    }

    @Test
    fun `load nonexistent project fails`() = runTest {
        assertTrue(repo.load(ProjectId("nonexistent")).isFailure)
    }

    @Test
    fun `save overwrites existing file`() = runTest {
        val project = sampleProject()
        repo.save(project).getOrThrow()

        val updated = project.copy(name = "Renamed")
        repo.save(updated).getOrThrow()

        val loaded = repo.load(project.id).getOrThrow()
        assertEquals("Renamed", loaded.name)
    }

    @Test
    fun `round-trip preserves theme and color role`() = runTest {
        val original = sampleProject().let { proj ->
            val screen = proj.screens.values.first()
            val textWithRole = (screen.root.slots[SlotId("content")] as SlotContent.One).node.let { node ->
                node.copy(
                    properties = node.properties + (PropertyKey("color") to Value.ColorRole("primary"))
                )
            }
            val updatedRoot = screen.root.copy(slots = mapOf(SlotId("content") to SlotContent.One(textWithRole)))
            val updatedScreen = screen.copy(root = updatedRoot)
            proj.copy(
                screens = mapOf(updatedScreen.id to updatedScreen),
                theme = io.github.chandu4221.designode.domain.model.ThemeSpec(
                    seedColor = 0xFF00FF00L,
                    isDark = true,
                    contrastLevel = 0.5,
                    style = "Vibrant",
                )
            )
        }

        repo.save(original).getOrThrow()
        val loaded = repo.load(original.id).getOrThrow()

        assertEquals(0xFF00FF00L, loaded.theme.seedColor)
        assertEquals(true, loaded.theme.isDark)
        assertEquals(0.5, loaded.theme.contrastLevel)
        assertEquals("Vibrant", loaded.theme.style)

        val loadedChild = loaded.screens.values.first().root.slots[SlotId("content")]?.nodes()?.firstOrNull()
        assertEquals(Value.ColorRole("primary"), loadedChild?.properties?.get(PropertyKey("color")))
    }
}