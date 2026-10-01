package io.github.chandu4221.designode.ui

import androidx.compose.ui.geometry.Offset
import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.port.ProjectRepository
import io.github.chandu4221.designode.domain.port.ProjectSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UndoRedoViewModelTest {

    private val fakeRepo = object : ProjectRepository {
        override suspend fun save(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun load(id: ProjectId): Result<Project> = Result.failure(Exception("Not found"))
        override suspend fun list(): Result<List<ProjectSummary>> = Result.success(emptyList())
        override suspend fun delete(id: ProjectId): Result<Unit> = Result.success(Unit)
    }

    private fun findFirst(node: AtomicNode, typeId: String): AtomicNode? {
        if (node.type.value == typeId) return node
        for (content in node.slots.values) {
            for (child in content.nodes()) {
                findFirst(child, typeId)?.let { return it }
            }
        }
        return null
    }

    @Test
    fun `drag and drop insert can be undone and redone`() {
        val viewModel = EditorViewModel(fakeRepo)
        val initialRoot = viewModel.state.value.root

        assertFalse(viewModel.state.value.canUndo)
        assertFalse(viewModel.state.value.canRedo)

        // Drop a Button into the Column (root Column is at (180, 400))
        viewModel.hitTestRegistry.record(
            id = findFirst(initialRoot, "Column")!!.id,
            rect = androidx.compose.ui.geometry.Rect(0f, 0f, 400f, 800f),
        )

        viewModel.beginDrag(ComponentTypeId("Button"))
        viewModel.updateDragPosition(Offset(200f, 200f))
        viewModel.commitDrag()

        assertTrue(viewModel.state.value.canUndo)
        assertFalse(viewModel.state.value.canRedo)
        val buttonNode = findFirst(viewModel.state.value.root, "Button")
        assertNotNull(buttonNode)

        // Undo
        viewModel.undo()
        assertFalse(viewModel.state.value.canUndo)
        assertTrue(viewModel.state.value.canRedo)
        assertNull(findFirst(viewModel.state.value.root, "Button"))

        // Redo
        viewModel.redo()
        assertTrue(viewModel.state.value.canUndo)
        assertFalse(viewModel.state.value.canRedo)
        val redoneButton = findFirst(viewModel.state.value.root, "Button")
        assertNotNull(redoneButton)
        assertEquals(buttonNode.id, redoneButton.id)
    }

    @Test
    fun `removeNode can be undone to restore node and redone to delete`() {
        val viewModel = EditorViewModel(fakeRepo)
        val column = findFirst(viewModel.state.value.root, "Column")!!

        viewModel.hitTestRegistry.record(
            id = column.id,
            rect = androidx.compose.ui.geometry.Rect(0f, 0f, 400f, 800f),
        )

        viewModel.beginDrag(ComponentTypeId("Text"))
        viewModel.updateDragPosition(Offset(100f, 100f))
        viewModel.commitDrag()

        val textNode = findFirst(viewModel.state.value.root, "Text")!!
        viewModel.removeNode(textNode.id)

        assertNull(findFirst(viewModel.state.value.root, "Text"))

        // Undo removal
        viewModel.undo()
        val restoredText = findFirst(viewModel.state.value.root, "Text")
        assertNotNull(restoredText)
        assertEquals(textNode.id, restoredText.id)

        // Redo removal
        viewModel.redo()
        assertNull(findFirst(viewModel.state.value.root, "Text"))
    }

    @Test
    fun `property change can be undone and redone`() {
        val viewModel = EditorViewModel(fakeRepo)
        val column = findFirst(viewModel.state.value.root, "Column")!!

        viewModel.hitTestRegistry.record(
            id = column.id,
            rect = androidx.compose.ui.geometry.Rect(0f, 0f, 400f, 800f),
        )

        viewModel.beginDrag(ComponentTypeId("Text"))
        viewModel.updateDragPosition(Offset(100f, 100f))
        viewModel.commitDrag()

        val textNode = findFirst(viewModel.state.value.root, "Text")!!

        viewModel.updateProperty(textNode.id, PropertyKey("text"), Value.Text("Custom Value"))
        assertEquals(
            Value.Text("Custom Value"),
            findFirst(viewModel.state.value.root, "Text")?.properties?.get(PropertyKey("text")),
        )

        // Undo property edit
        viewModel.undo()
        assertEquals(
            null,
            findFirst(viewModel.state.value.root, "Text")?.properties?.get(PropertyKey("text")),
        )

        // Redo property edit
        viewModel.redo()
        assertEquals(
            Value.Text("Custom Value"),
            findFirst(viewModel.state.value.root, "Text")?.properties?.get(PropertyKey("text")),
        )
    }

    @Test
    fun `multi-screen undo automatically switches screen to where action occurred`() {
        val viewModel = EditorViewModel(fakeRepo)
        val screen1Id = viewModel.state.value.activeScreenId!!
        val column = findFirst(viewModel.state.value.root, "Column")!!

        viewModel.hitTestRegistry.record(
            id = column.id,
            rect = androidx.compose.ui.geometry.Rect(0f, 0f, 400f, 800f),
        )

        viewModel.beginDrag(ComponentTypeId("Button"))
        viewModel.updateDragPosition(Offset(100f, 100f))
        viewModel.commitDrag()

        assertNotNull(findFirst(viewModel.state.value.root, "Button"))

        // Add a second screen
        viewModel.addScreen("SecondScreen")
        val screen2Id = viewModel.state.value.activeScreenId!!
        assertEquals(screen2Id, viewModel.state.value.activeScreenId)

        // Undo the button add that happened on Screen 1
        viewModel.undo()

        // Active screen should have switched back to Screen 1
        assertEquals(screen1Id, viewModel.state.value.activeScreenId)
        assertNull(findFirst(viewModel.state.value.root, "Button"))

        // Redo should also restore to Screen 1
        viewModel.redo()
        assertEquals(screen1Id, viewModel.state.value.activeScreenId)
        assertNotNull(findFirst(viewModel.state.value.root, "Button"))
    }
}
