package io.github.chandu4221.designode.ui

import androidx.compose.ui.geometry.Offset
import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.port.ProjectRepository
import io.github.chandu4221.designode.domain.port.ProjectSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ClipboardViewModelTest {

    private val fakeRepo = object : ProjectRepository {
        override suspend fun save(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun load(id: ProjectId): Result<Project> = Result.failure(Exception("Not found"))
        override suspend fun list(): Result<List<ProjectSummary>> = Result.success(emptyList())
        override suspend fun delete(id: ProjectId): Result<Unit> = Result.success(Unit)
    }

    private fun findNodes(node: AtomicNode, typeId: String): List<AtomicNode> {
        val result = mutableListOf<AtomicNode>()
        if (node.type.value == typeId) result.add(node)
        for (content in node.slots.values) {
            for (child in content.nodes()) {
                result.addAll(findNodes(child, typeId))
            }
        }
        return result
    }

    @Test
    fun `copy and paste duplicates node with new ID into container`() {
        val viewModel = EditorViewModel(fakeRepo)
        val initialRoot = viewModel.state.value.root
        val column = findNodes(initialRoot, "Column").first()

        viewModel.hitTestRegistry.record(
            id = column.id,
            rect = androidx.compose.ui.geometry.Rect(0f, 0f, 400f, 800f),
        )

        // Drop a Button
        viewModel.beginDrag(ComponentTypeId("Button"))
        viewModel.updateDragPosition(Offset(100f, 100f))
        viewModel.commitDrag()

        val button1 = findNodes(viewModel.state.value.root, "Button").first()
        viewModel.selectNode(button1.id)

        assertFalse(viewModel.state.value.hasClipboard)
        viewModel.copySelected()
        assertTrue(viewModel.state.value.hasClipboard)

        // Paste
        viewModel.paste()

        val buttons = findNodes(viewModel.state.value.root, "Button")
        assertEquals(2, buttons.size)
        assertNotEquals(buttons[0].id, buttons[1].id)
        assertEquals(buttons[1].id, viewModel.state.value.selectedId)
    }

    @Test
    fun `duplicateSelected inserts adjacent sibling with new ID and supports undo`() {
        val viewModel = EditorViewModel(fakeRepo)
        val initialRoot = viewModel.state.value.root
        val column = findNodes(initialRoot, "Column").first()

        viewModel.hitTestRegistry.record(
            id = column.id,
            rect = androidx.compose.ui.geometry.Rect(0f, 0f, 400f, 800f),
        )

        viewModel.beginDrag(ComponentTypeId("Button"))
        viewModel.updateDragPosition(Offset(100f, 100f))
        viewModel.commitDrag()

        val originalButton = findNodes(viewModel.state.value.root, "Button").first()
        viewModel.selectNode(originalButton.id)

        viewModel.duplicateSelected()

        val buttons = findNodes(viewModel.state.value.root, "Button")
        assertEquals(2, buttons.size)
        assertNotEquals(originalButton.id, buttons[1].id)
        assertEquals(buttons[1].id, viewModel.state.value.selectedId)

        // Undo duplicate
        viewModel.undo()
        val buttonsAfterUndo = findNodes(viewModel.state.value.root, "Button")
        assertEquals(1, buttonsAfterUndo.size)
        assertEquals(originalButton.id, buttonsAfterUndo[0].id)
    }

    @Test
    fun `deselect clears selectedId`() {
        val viewModel = EditorViewModel(fakeRepo)
        val column = findNodes(viewModel.state.value.root, "Column").first()
        viewModel.selectNode(column.id)
        assertEquals(column.id, viewModel.state.value.selectedId)

        viewModel.deselect()
        assertNull(viewModel.state.value.selectedId)
    }
}
