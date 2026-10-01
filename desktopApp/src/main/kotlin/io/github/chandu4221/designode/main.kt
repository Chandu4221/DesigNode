package io.github.chandu4221.designode

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.chandu4221.designode.persistence.JsonProjectRepository
import io.github.chandu4221.designode.ui.App
import io.github.chandu4221.designode.ui.EditorViewModel
import java.nio.file.Path
import kotlin.io.path.createDirectories

fun main() = application {
    val storageDir: Path = Path
        .of(System.getProperty("user.home"), "DesigNode", "projects")
        .also { it.createDirectories() }

    val repository = JsonProjectRepository(storageDir)
    val viewModel = EditorViewModel(repository)

    Window(
        onCloseRequest = ::exitApplication,
        title = "DesigNode",
    ) {
        App(viewModel = viewModel)
    }
}