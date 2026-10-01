package io.github.chandu4221.designode.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.Project
import io.github.chandu4221.designode.domain.model.ProjectId
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.nodes
import io.github.chandu4221.designode.domain.port.ProjectRepository
import io.github.chandu4221.designode.domain.port.ProjectSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EditorDropTargetTest {

    private val fakeRepo = object : ProjectRepository {
        override suspend fun save(project: Project): Result<Unit> = Result.success(Unit)
        override suspend fun load(id: ProjectId): Result<Project> = Result.failure(Exception("Not found"))
        override suspend fun list(): Result<List<ProjectSummary>> = Result.success(emptyList())
        override suspend fun delete(id: ProjectId): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `dragging TopAppBar over content redirects to Scaffold topBar`() {
        val viewModel = EditorViewModel(fakeRepo)
        val scaffoldId = viewModel.state.value.root.id
        val columnId = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first().id

        // Record bounds simulating canvas layout
        viewModel.hitTestRegistry.record(scaffoldId, Rect(0f, 0f, 360f, 720f))
        viewModel.hitTestRegistry.record(columnId, Rect(0f, 56f, 360f, 664f))

        // Drag TopAppBar over the Column area (e.g. y = 300)
        viewModel.beginDrag(ComponentTypeId("TopAppBar"))
        viewModel.updateDragPosition(Offset(180f, 300f))

        val dropTarget = viewModel.state.value.dropTarget
        assertNotNull(dropTarget)
        assertEquals(scaffoldId, dropTarget.nodeId)
        assertEquals(SlotId("topBar"), dropTarget.slotId)

        // Commit drag and verify insertion in Scaffold.topBar
        viewModel.commitDrag()

        val updatedRoot = viewModel.state.value.root
        val topBarChildren = updatedRoot.slots[SlotId("topBar")]?.nodes().orEmpty()
        assertEquals(1, topBarChildren.size)
        assertEquals(ComponentTypeId("TopAppBar"), topBarChildren.first().type)

        // Verify Column content was NOT affected
        val columnChildren = updatedRoot.slots[SlotId("content")]?.nodes()?.first()?.slots?.get(SlotId("content"))?.nodes().orEmpty()
        assertTrue(columnChildren.isEmpty())
    }

    @Test
    fun `dragging NavigationBar over content redirects to Scaffold bottomBar`() {
        val viewModel = EditorViewModel(fakeRepo)
        val scaffoldId = viewModel.state.value.root.id
        val columnId = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first().id

        viewModel.hitTestRegistry.record(scaffoldId, Rect(0f, 0f, 360f, 720f))
        viewModel.hitTestRegistry.record(columnId, Rect(0f, 56f, 360f, 664f))

        viewModel.beginDrag(ComponentTypeId("NavigationBar"))
        viewModel.updateDragPosition(Offset(180f, 250f))

        val dropTarget = viewModel.state.value.dropTarget
        assertNotNull(dropTarget)
        assertEquals(scaffoldId, dropTarget.nodeId)
        assertEquals(SlotId("bottomBar"), dropTarget.slotId)

        viewModel.commitDrag()

        val updatedRoot = viewModel.state.value.root
        val bottomBarChildren = updatedRoot.slots[SlotId("bottomBar")]?.nodes().orEmpty()
        assertEquals(1, bottomBarChildren.size)
        assertEquals(ComponentTypeId("NavigationBar"), bottomBarChildren.first().type)
    }

    @Test
    fun `dragging generic Button over content targets Column children`() {
        val viewModel = EditorViewModel(fakeRepo)
        val scaffoldId = viewModel.state.value.root.id
        val columnId = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first().id

        viewModel.hitTestRegistry.record(scaffoldId, Rect(0f, 0f, 360f, 720f))
        viewModel.hitTestRegistry.record(columnId, Rect(0f, 56f, 360f, 664f))

        viewModel.beginDrag(ComponentTypeId("Button"))
        viewModel.updateDragPosition(Offset(180f, 200f))

        val dropTarget = viewModel.state.value.dropTarget
        assertNotNull(dropTarget)
        assertEquals(columnId, dropTarget.nodeId)
        assertEquals(SlotId("content"), dropTarget.slotId)

        viewModel.commitDrag()

        val updatedRoot = viewModel.state.value.root
        val columnChildren = updatedRoot.slots[SlotId("content")]?.nodes()?.first()?.slots?.get(SlotId("content"))?.nodes().orEmpty()
        assertEquals(1, columnChildren.size)
        assertEquals(ComponentTypeId("Button"), columnChildren.first().type)
    }

    @Test
    fun `direct hit on slot drop zone targets that specific slot`() {
        val viewModel = EditorViewModel(fakeRepo)
        val scaffoldId = viewModel.state.value.root.id

        viewModel.hitTestRegistry.recordSlot(scaffoldId, SlotId("floatingActionButton"), Rect(280f, 620f, 336f, 676f))

        viewModel.beginDrag(ComponentTypeId("FloatingActionButton"))
        viewModel.updateDragPosition(Offset(300f, 650f))

        val dropTarget = viewModel.state.value.dropTarget
        assertNotNull(dropTarget)
        assertEquals(scaffoldId, dropTarget.nodeId)
        assertEquals(SlotId("floatingActionButton"), dropTarget.slotId)
    }

    @Test
    fun `dragging Button directly over Scaffold routes into content Column`() {
        val viewModel = EditorViewModel(fakeRepo)
        val scaffoldId = viewModel.state.value.root.id
        val columnId = viewModel.state.value.root.slots[SlotId("content")]!!.nodes().first().id

        viewModel.hitTestRegistry.record(scaffoldId, Rect(0f, 0f, 360f, 720f))

        viewModel.beginDrag(ComponentTypeId("Button"))
        viewModel.updateDragPosition(Offset(180f, 200f))

        val dropTarget = viewModel.state.value.dropTarget
        assertNotNull(dropTarget)
        assertEquals(columnId, dropTarget.nodeId)
        assertEquals(SlotId("content"), dropTarget.slotId)
    }
}
