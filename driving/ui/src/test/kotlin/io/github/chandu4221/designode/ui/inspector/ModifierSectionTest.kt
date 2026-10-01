package io.github.chandu4221.designode.ui.inspector

import io.github.chandu4221.designode.domain.model.*
import io.github.chandu4221.designode.domain.port.ProjectRepository
import io.github.chandu4221.designode.domain.port.ProjectSummary
import io.github.chandu4221.designode.ui.EditorViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModifierSectionTest {

    private val fakeRepo = object : ProjectRepository {
        override suspend fun save(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun load(id: ProjectId): Result<Project> = Result.failure(Exception("Not found"))
        override suspend fun list(): Result<List<ProjectSummary>> = Result.success(emptyList())
        override suspend fun delete(id: ProjectId): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `updating modifiers on a node modifies state and records undo`() {
        val viewModel = EditorViewModel(fakeRepo)
        val initialRoot = viewModel.state.value.root
        val column = initialRoot.slots[SlotId("content")]!!.nodes().first()

        assertEquals(emptyList(), column.modifiers)

        val newModifiers = listOf(
            ModifierToken.Padding(16f),
            ModifierToken.FillMaxWidth(1f),
        )

        viewModel.updateModifiers(column.id, newModifiers)

        val updatedColumn = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first()
        assertEquals(2, updatedColumn.modifiers.size)
        assertEquals(ModifierToken.Padding(16f), updatedColumn.modifiers[0])
        assertEquals(ModifierToken.FillMaxWidth(1f), updatedColumn.modifiers[1])

        assertTrue(viewModel.state.value.canUndo)

        // Undo modifier change
        viewModel.undo()

        val undoneColumn = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first()
        assertEquals(emptyList(), undoneColumn.modifiers)
        assertTrue(viewModel.state.value.canRedo)

        // Redo modifier change
        viewModel.redo()

        val redoneColumn = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first()
        assertEquals(2, redoneColumn.modifiers.size)
    }

    @Test
    fun `removing modifier from list updates state`() {
        val viewModel = EditorViewModel(fakeRepo)
        val initialRoot = viewModel.state.value.root
        val column = initialRoot.slots[SlotId("content")]!!.nodes().first()

        val initialModifiers = listOf(
            ModifierToken.FillMaxWidth(1f),
            ModifierToken.FillMaxHeight(1f),
            ModifierToken.Padding(8f),
        )

        viewModel.updateModifiers(column.id, initialModifiers)

        val updatedModifiers = initialModifiers.filterNot { it is ModifierToken.Padding }
        viewModel.updateModifiers(column.id, updatedModifiers)

        val currentColumn = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first()
        assertEquals(2, currentColumn.modifiers.size)
        assertFalse(currentColumn.modifiers.any { it is ModifierToken.Padding })
    }

    @Test
    fun `editing modifier value updates state correctly`() {
        val viewModel = EditorViewModel(fakeRepo)
        val initialRoot = viewModel.state.value.root
        val column = initialRoot.slots[SlotId("content")]!!.nodes().first()

        viewModel.updateModifiers(column.id, listOf(ModifierToken.Padding(16f)))

        viewModel.updateModifiers(column.id, listOf(ModifierToken.Padding(24f)))

        val currentColumn = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first()
        assertEquals(1, currentColumn.modifiers.size)
        assertEquals(ModifierToken.Padding(24f), currentColumn.modifiers.first())
    }
}
