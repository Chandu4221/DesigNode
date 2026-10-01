package io.github.chandu4221.designode.ui

import io.github.chandu4221.designode.domain.model.Project
import io.github.chandu4221.designode.domain.model.ProjectId
import io.github.chandu4221.designode.domain.model.ThemeSpec
import io.github.chandu4221.designode.domain.port.ProjectRepository
import io.github.chandu4221.designode.domain.port.ProjectSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThemeViewModelTest {

    private val saveLatch = java.util.concurrent.CountDownLatch(1)
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
    fun `default theme state is initialized properly`() {
        val viewModel = EditorViewModel(fakeRepo)
        val theme = viewModel.state.value.theme

        assertEquals(ThemeSpec.Default.seedColor, theme.seedColor)
        assertEquals(true, theme.isDark)
        assertEquals(0.0, theme.contrastLevel)
        assertEquals("TonalSpot", theme.style)
        assertFalse(viewModel.state.value.themeDialogOpen)
    }

    @Test
    fun `theme dialog open and close state updates`() {
        val viewModel = EditorViewModel(fakeRepo)
        viewModel.openThemeDialog()
        assertTrue(viewModel.state.value.themeDialogOpen)

        viewModel.closeThemeDialog()
        assertFalse(viewModel.state.value.themeDialogOpen)
    }

    @Test
    fun `theme seed color and dark mode updates`() {
        val viewModel = EditorViewModel(fakeRepo)

        viewModel.updateThemeSeedColor(0xFF0061A4L)
        assertEquals(0xFF0061A4L, viewModel.state.value.theme.seedColor)

        viewModel.toggleThemeDarkMode()
        assertFalse(viewModel.state.value.theme.isDark)

        viewModel.toggleThemeDarkMode()
        assertTrue(viewModel.state.value.theme.isDark)

        viewModel.updateThemeContrast(0.5)
        assertEquals(0.5, viewModel.state.value.theme.contrastLevel)

        viewModel.updateThemeStyle("Vibrant")
        assertEquals("Vibrant", viewModel.state.value.theme.style)
    }

    @Test
    fun `theme is saved with project and loaded back`() {
        val viewModel = EditorViewModel(fakeRepo)
        viewModel.updateTheme(
            ThemeSpec(
                seedColor = 0xFFBA1A1AL,
                isDark = true,
                contrastLevel = -0.5,
                style = "Expressive",
            )
        )
        viewModel.save()
        assertTrue(saveLatch.await(2, java.util.concurrent.TimeUnit.SECONDS))

        val savedProject = fakeRepo.lastSaved
        assertEquals(0xFFBA1A1AL, savedProject?.theme?.seedColor)
        assertEquals(true, savedProject?.theme?.isDark)
        assertEquals(-0.5, savedProject?.theme?.contrastLevel)
        assertEquals("Expressive", savedProject?.theme?.style)
    }
}
