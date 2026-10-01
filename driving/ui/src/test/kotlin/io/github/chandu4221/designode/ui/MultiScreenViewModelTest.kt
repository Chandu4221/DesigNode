package io.github.chandu4221.designode.ui

import io.github.chandu4221.designode.domain.model.ComponentTypeId
import io.github.chandu4221.designode.domain.model.Project
import io.github.chandu4221.designode.domain.model.ProjectId
import io.github.chandu4221.designode.domain.model.PropertyKey
import io.github.chandu4221.designode.domain.model.SlotId
import io.github.chandu4221.designode.domain.model.Value
import io.github.chandu4221.designode.domain.port.ProjectRepository
import io.github.chandu4221.designode.domain.port.ProjectSummary
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MultiScreenViewModelTest {

    private val saveLatch = CountDownLatch(1)
    private val fakeRepo = object : ProjectRepository {
        var lastSaved: Project? = null
        override suspend fun save(project: Project): Result<Unit> {
            lastSaved = project
            saveLatch.countDown()
            return Result.success(Unit)
        }
        override suspend fun load(id: ProjectId): Result<Project> =
            lastSaved?.let { Result.success(it) } ?: Result.failure(Exception("Not found"))
        override suspend fun list(): Result<List<ProjectSummary>> = Result.success(emptyList())
        override suspend fun delete(id: ProjectId): Result<Unit> = Result.success(Unit)
    }

    @Test
    fun `initial project has Main screen as active and start screen`() {
        val viewModel = EditorViewModel(fakeRepo)
        val state = viewModel.state.value

        assertEquals(1, state.screens.size)
        val first = state.screens.first()
        assertEquals("Main", first.name)
        assertTrue(first.isStart)
        assertEquals(first.id, state.activeScreenId)
    }

    @Test
    fun `addScreen creates new screen and makes it active`() {
        val viewModel = EditorViewModel(fakeRepo)
        viewModel.addScreen("Profile")

        val state = viewModel.state.value
        assertEquals(2, state.screens.size)
        assertEquals("Main", state.screens[0].name)
        assertEquals("Profile", state.screens[1].name)
        assertFalse(state.screens[1].isStart)
        assertEquals(state.screens[1].id, state.activeScreenId)
    }

    @Test
    fun `switchScreen changes active screen without affecting other screens`() {
        val viewModel = EditorViewModel(fakeRepo)
        val screen1Id = viewModel.state.value.activeScreenId!!

        viewModel.addScreen("Second")
        val screen2Id = viewModel.state.value.activeScreenId!!
        assertNotEquals(screen1Id, screen2Id)

        // Switch back to screen 1
        viewModel.switchScreen(screen1Id)
        assertEquals(screen1Id, viewModel.state.value.activeScreenId)

        // Switch to screen 2
        viewModel.switchScreen(screen2Id)
        assertEquals(screen2Id, viewModel.state.value.activeScreenId)
    }

    @Test
    fun `renameScreen updates screen tab name`() {
        val viewModel = EditorViewModel(fakeRepo)
        val screenId = viewModel.state.value.activeScreenId!!

        viewModel.renameScreen(screenId, "Dashboard")
        assertEquals("Dashboard", viewModel.state.value.screens.first().name)
    }

    @Test
    fun `removeScreen deletes screen and switches active screen`() {
        val viewModel = EditorViewModel(fakeRepo)
        val screen1Id = viewModel.state.value.activeScreenId!!

        viewModel.addScreen("Details")
        val screen2Id = viewModel.state.value.activeScreenId!!
        assertEquals(2, viewModel.state.value.screens.size)

        viewModel.removeScreen(screen2Id)
        assertEquals(1, viewModel.state.value.screens.size)
        assertEquals(screen1Id, viewModel.state.value.activeScreenId)

        // Attempting to remove the last screen should be ignored
        viewModel.removeScreen(screen1Id)
        assertEquals(1, viewModel.state.value.screens.size)
    }

    @Test
    fun `setStartScreen changes the start screen`() {
        val viewModel = EditorViewModel(fakeRepo)
        val screen1Id = viewModel.state.value.activeScreenId!!

        viewModel.addScreen("Login")
        val screen2Id = viewModel.state.value.activeScreenId!!

        viewModel.setStartScreen(screen2Id)

        val screen1Tab = viewModel.state.value.screens.first { it.id == screen1Id }
        val screen2Tab = viewModel.state.value.screens.first { it.id == screen2Id }

        assertFalse(screen1Tab.isStart)
        assertTrue(screen2Tab.isStart)
    }

    @Test
    fun `multi-screen export generates functions for all screens`() {
        val viewModel = EditorViewModel(fakeRepo)
        viewModel.renameScreen(viewModel.state.value.activeScreenId!!, "Home")
        viewModel.addScreen("Settings")

        val code = viewModel.generateCode()
        assertTrue(code.contains("fun HomeScreen() {"))
        assertTrue(code.contains("fun SettingsScreen() {"))
    }

    @Test
    fun `save and load round-trips all screens`() {
        val viewModel = EditorViewModel(fakeRepo)
        viewModel.renameScreen(viewModel.state.value.activeScreenId!!, "FirstScreen")
        viewModel.addScreen("SecondScreen")

        viewModel.save()
        assertTrue(saveLatch.await(2, TimeUnit.SECONDS))

        val saved = fakeRepo.lastSaved
        assertNotNull(saved)
        assertEquals(2, saved.screens.size)
        assertTrue(saved.screens.values.any { it.name == "FirstScreen" })
        assertTrue(saved.screens.values.any { it.name == "SecondScreen" })
    }
}
